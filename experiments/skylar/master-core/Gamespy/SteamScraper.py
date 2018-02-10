import asyncio, aiohttp

import Gamespy


class SteamScraper(Gamespy.BaseScraper):

    WORKER_LIMIT = 1

    async def init(self):
        #await asyncio.sleep(3)
        # Todo; Using /experiments/skylar/rate-limit-tester/test.py
        # > If started with --populate-redis, fetch all apps from Steam and well, populate Redis
        # Then, await Redis' SteamApps list for jobs until the application is closed
        print(Gamespy.args.populate_redis_steamapps)

        if Gamespy.args.populate_redis_steamapps:
            asyncio.ensure_future(self.populate_redis_with_steamapps())

            # Set to False so that the other workers don't populate Redis!
            Gamespy.args.populate_redis_steamapps = False

        while True:
            task = await self.await_task()
            print(task)


    async def await_task(self):
        with await Gamespy.redisPool as redis:
            task = await redis.execute("blpop", "/SteamScraper/WorkerAppList", 0)
            # await asyncio.sleep(1)

        return task


    async def populate_redis_with_steamapps(self):
        print(1)
        await asyncio.sleep(10)