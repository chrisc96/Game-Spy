import asyncio

import Gamespy


class SteamScraper(Gamespy.BaseScraper):

    WORKER_LIMIT = 1

    async def worker(self):
        await asyncio.sleep(3)
        # Todo; Using /experiments/skylar/rate-limit-tester/test.py
        # > If started with --populate-redis, fetch all apps from Steam and well, populate Redis
        # Then, await Redis' SteamApps list for jobs until the application is closed