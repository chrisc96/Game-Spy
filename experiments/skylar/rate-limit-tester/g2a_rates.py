import threading
import time
import queue
import json
import ctypes
import urllib.request
import sys

# Variables for rates of processing
timeWhenStarted = time.time()

# Overrides user agent (g2a blocks spiders/bots)
class AppURLopener(urllib.request.FancyURLopener):
    version = "Mozilla/5.0"

'''
Returns the 'numFound' value via g2a api. This represents the number of pages
of JSON data that G2A has indexed. Useful for scraping all their data.
'''
def getNumListings():
	opener = AppURLopener()
	with opener.open('https://www.g2a.com/lucene/search/filter') as fh:
		response = json.loads(fh.read())

		if (int(response["numFound"])):
			return (int(response["numFound"]))
		else:
			return 0

'''
Returns a JSON formatted list of g2a listings
(presuming no errors).
Let's say one page has 10 listings. At the start, listingPK == 0.
When we parse the page with ten listings, this method should be called again
with a listingPK of 10 to make sure we start where we left off.
'''
def fetchG2APage(listingPK):
	try:
		opener = AppURLopener()
		with opener.open("https://www.g2a.com/lucene/search/filter?=&start=%i" % listingPK) as fh:
			print("Connection accepted - App beginning %i" % listingPK)
			return json.loads(fh.read())

	except opener.HTTPError:
		print("Connection refused")
	except:
		print("Unknown error?")


def fetchG2APriceByID(idNum):
	try:
		opener = AppURLopener()
		with opener.open("https://www.g2a.com/marketplace/product/auctions/?id=%i" % idNum) as fh:
			print("Pricing connection accepted")
			data = json.loads(fh.read())
			return data["lowest_price"]

	except opener.HTTPError:
		print("Connection refused")
	except:
		print("Unknown error?")

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
