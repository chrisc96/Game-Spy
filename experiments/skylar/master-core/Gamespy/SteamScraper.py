import asyncio

import Gamespy


class SteamScraper(Gamespy.BaseScraper):

    WORKER_LIMIT = 1

    async def worker(self):
        await asyncio.sleep(0)