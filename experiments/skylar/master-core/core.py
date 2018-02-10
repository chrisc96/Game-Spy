# This is our master scraper, the core of it works by launching several tasks under asyncio
# which have been extended from BaseScraper

import argparse
import asyncio
import uvloop
import os.path

import Gamespy


# Begins
if __name__ == "__main__":
    try:
        # Get our instance argumentsjssss
        parser = argparse.ArgumentParser(description="Game Spy Master Scraper")
        parser.add_argument("-config-path", type=str, default="../config")
        parser.add_argument("--populate-redis-steamapps", action="store_true")

        Gamespy.args = parser.parse_args()


        # Make sure our config files exist
        if not os.path.isfile(Gamespy.args.config_path + "/mysql.json"):
            exit(Gamespy.args.config_path + "/mysql.json Could not be found")

        if not os.path.isfile(Gamespy.args.config_path + "/redis.json"):
            exit(Gamespy.args.config_path + "/redis.json Could not be found")


        # Start up AsyncIO
        asyncio.set_event_loop_policy(uvloop.EventLoopPolicy())
        loop = asyncio.get_event_loop()

        # Connect to Redis
        loop.run_until_complete(Gamespy.preconnect_redis())

        # Start up a task for each of our scrapers
        tasks = []
        for scraper in Gamespy.SCRAPERS:
            instance = scraper()

            for i in range(instance.WORKER_LIMIT):
                tasks.append(asyncio.ensure_future(instance.init()))

        # Wait for all tasks to complete
        loop.run_until_complete(asyncio.wait(tasks))
        loop.close()

    except KeyboardInterrupt:
        pass