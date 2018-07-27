import threading
import time
import urllib.request
import queue
import json
import ctypes

# How many parallel requests do you want to limit this to?
WORKER_LIMIT = 1

# Variables for rates of processing
timeWhenStarted = time.time()

'''
'' Fetches information on the app from Steam's API
'' Returns array of app data if successful, otherwise None
'' @param app:dict - Retrieved from fetchAllSteamApps list '''


def fetchAppInformation(app):
		with urllib.request.urlopen("http://store.steampowered.com/api/appdetails?appids=%i&cc=us&key=CA460E3690A493219F7682B4FC48CEDF" % app["appid"]) as fh:
				appInformation = fh.read()

		appInformation = json.loads(appInformation)

		if appInformation[str(app["appid"])]["success"]:
				return appInformation[str(app["appid"])]["data"]

		else:
				return None


'''
'' Fetch a list of Steam games from Steam's API '''


def fetchAllSteamApps():
        try:
                with urllib.request.urlopen("http://api.steampowered.com/ISteamApps/GetAppList/v2") as fh:
                        steamApps = fh.read()

                return json.loads(steamApps)
        except:
                # logging.save(original exception details)
                print("Could not access Steam's App List")
                # raise Exception("Could not access Steam's App List") from None



# Feed masterQueue with jobs
steamApps = fetchAllSteamApps()
steamApps = steamApps["applist"]["apps"]  # bullshitakken

while steamApps:
	try:
		print("Polling ID %s" % steamApps[0])
		appInformation = fetchAppInformation(steamApps[0])


		if appInformation and appInformation["type"] == "game":  # Is a game
			if appInformation["is_free"]:
				print(appInformation["name"], "is Free!", appInformation["steam_appid"])

			elif "price_overview" not in appInformation:
				print("PRICE OVERVIEW DOES NOT EXIST FOR ID", appInformation["steam_appid"])

			else:
				floatPrice = round(int(appInformation["price_overview"]["final"]) / 100, 2)
				print(appInformation["name"], "$%.2f (USD)" % floatPrice)

		else:
			print("Not a game, but polled this ID")

		# Requst succeeded, remove element from array and sleep
		steamApps.remove(steamApps[0])
		time.sleep(0.85)

	except urllib.request.HTTPError:
		print("An error occured, sleeping for 60 seconds")
		time.sleep(60)

	except:
		pass
