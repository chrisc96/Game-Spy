import threading
import time
import queue
import json
import ctypes
import urllib.request
import threading

# Variables for rates of processing
timeWhenStarted = time.time()

WORKER_COUNT = 10

'''
'' Returns the 'numFound' value via g2a api. This represents the number of
'' app listings that G2A has indexed. Useful to know for scraping all their data.
'''
def getNumListings():
    try:
        request = urllib.request.Request("https://www.g2a.com/lucene/search/filter")
        request.add_header("User-Agent", "Mozilla/5.0")
        with urllib.request.urlopen(request) as fh:
            response = json.loads(fh.read())

            if (int(response["numFound"])):
                return (int(response["numFound"]))
            else:
                return 0

    except urllib.request.HTTPError:
        val = 60
        print("HTTP Error - Couldn't receive number of listings. Will try again in %is" % val)
        time.sleep(val)


'''
'' Returns a single page of JSON g2a app listings
'' @param listingPK int represents the start appID of that page
'''
def fetchG2APage(listingPK):
    try:
        request = urllib.request.Request("https://www.g2a.com/lucene/search/filter?=&start=%i" % listingPK)
        request.add_header("User-Agent", "Mozilla/5.0")
        with urllib.request.urlopen(request) as fh:
            return json.loads(fh.read())

    except urllib.request.HTTPError:
        print("HTTP Error")
        time.sleep(60)

'''
'' Returns the price (exc. shield for now) in the default currency (???)
'' of that game.
'' @param idNum int the ID number of the game to get the price of
'''
def fetchG2APriceByID(idNum):
    try:
        request = urllib.request.Request("https://www.g2a.com/marketplace/product/auctions/?id=%i" % idNum)
        request.add_header("User-Agent", "Mozilla/5.0")
        with urllib.request.urlopen(request) as fh:
            data = json.loads(fh.read())
            return float(data["lowest_price"])

    except urllib.request.HTTPError:
        print("HTTP Error")
        time.sleep(60)
    except:
        pass

'''
'' Each thread's DOWORK function. Power in numbers.
'' Yay for threads.
'''
def worker(i, taskQueue):
    try:
        if taskQueue.empty():
            pass
        else:
            for id in iter(taskQueue.get, None):
                # mutable stringbuilding
                l = []
                l.append("https://www.g2a.com/lucene/search/filter?=&start=")
                l.append(id)
                url = ''.join(l)

                pageInfo = fetchG2APage(int(id))
                if (pageInfo is not None):
                    listing = pageInfo["docs"][0] # remove numFound, start, docs tags
                    if (listing["id"]):
                        id = int(listing["id"])
                    if (listing["name"]):
                        name = listing["name"]

                    # get name, id, all the other juicy details here via same method as above -> listing["jsonTag"]

                    price = fetchG2APriceByID(id)
                    if (price is not None):
                        processedSoFar = numListings - (taskQueue.qsize() - 1)
                        print("(%i)    Game listing with ID of %i and name %s costs %.2f" % (processedSoFar, id, name, price))
    except urllib.request.HTTPError:
        print("HTTP Error")
        time.sleep(60)
    except:
        pass



'''
'' Main Execution. Sets up / starts multi-threading task
'''
urlQueue = queue.Queue()

# Makes sure we have a value before we proceed
numListings = None
while (numListings is None):
    numListings = getNumListings()

workerList  = []

# Populate queue with ID's for workers to utilise
for id in range(0, numListings):
    urlQueue.put("%i" % id)

for i in range(WORKER_COUNT):
    thread = threading.Thread(target = worker, args = (i, urlQueue))
    thread.daemon = False
    thread.start()
    workerList.append(thread)

# After all tasks have been completed, peacefully terminate the workers
for i in range(WORKER_COUNT):
    urlQueue.put(None)
