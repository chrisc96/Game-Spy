import threading
import time
import queue
import json
import ctypes
import urllib.request
import threading

# Variables for rates of processing
timeWhenStarted = time.time()

WORKER_COUNT = 15

'''
Returns the 'numFound' value via g2a api. This represents the number of pages
of JSON data that G2A has indexed. Useful for scraping all their data.
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
        print("HTTP Error")
        time.sleep(60)
    except:
        pass


'''
Returns a JSON formatted list of g2a listings
(presuming no errors).
Let's say one page has 10 listings. At the start, listingPK == 0.
When we parse the page with ten listings, this method should be called again
with a listingPK of 10 to make sure we start where we left off.
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
    except:
        pass

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


def worker(i, taskQueue):
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
            listing = pageInfo["docs"][0] # remove numFound, start, docs tags
            id = int(listing["id"])
            name = listing["name"]

            price = fetchG2APriceByID(id)
            processedSoFar = numListings - (taskQueue.qsize() - 1)
            print("(%i)    Game listing with ID of %i costs %.2f" % (processedSoFar, id, price))

urlQueue = queue.Queue()
numListings = getNumListings()
workerList  = []
processedSoFar = 0

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

'''
numListings = getNumListings()
currListing = 0

while (currListing < numListings):
	pageInfo = fetchG2APage(currListing)
	pageInfo = pageInfo["docs"] # remove numFound, start, docs tags

	# parse all N games on the page, then adjust gamesPerPage to reflect N parsed games
	gamesPerPage = 0
	while (gamesPerPage < len(pageInfo)):
		info = pageInfo[gamesPerPage]
		id = info["id"]
		price = fetchG2APriceByID(id)
		print(price)
		gamesPerPage += 1

	currListing += gamesPerPage
'''
