import asyncio
import aiohttp
import json

import Gamespy


class SteamScraper(Gamespy.BaseScraper):

    WORKER_LIMIT = 1


    '''
    '' Start waiting for jobs, populate applist if requested '''
    async def init(self):
        # Create a Redis connection exclusive to this instance of SteamScraper so we can use blocking
        # pop without hogging a redis_pool connection
        await self.create_exclusive_redis()


        # Populate Redis queue if required
        if Gamespy.args.populate_redis_steamapps:
            asyncio.ensure_future(self.populate_redis_with_steamapps())

            # Set to False so that the other workers don't populate Redis!
            Gamespy.args.populate_redis_steamapps = False


        # Start waiting for tasks
        while True:
            task = await self.await_task()
            print(task)


        # Close exclusive Redis
        self.exclusiveRedis.close()
        await self.exclusiveRedis.wait_closed()




    '''
    '' Use Redis blocking list pop to wait for tasks in no particular order '''
    async def await_task(self):
        task = await self.exclusiveRedis.blpop("/SteamScraper/WorkerAppList", 0)
        return task


    '''
    '' Fetches all Steam App ID's from Steam's public store API '''
    async def fetchAllSteamApps(self):
        try:
            async with aiohttp.ClientSession() as session:
                async with session.get("http://api.steampowered.com/ISteamApps/GetAppList/v2") as response:
                    body = await response.read()

            apiResponse = json.loads(body)
            return apiResponse["applist"]["apps"]

        except ValueError: # Invalid JSON
            return None


    '''
    '' Insert all App ID's and data into Redis '''
    async def insert_applist_to_redis(self, appList):
        # Convert appList into chunks for redis' pipeline
        iterations = int(len(appList) / 1024) + 1
        chunks = [appList[i*1024 : i*1024 + 1024] for i in range(iterations)]

        with await Gamespy.redisPool as redis:
            pipe = redis.pipeline()
            pipe.delete("/SteamScraper/WorkerAppList")

            # Chunk SteamApps into Redis
            for chunk in chunks:
                chunk = ["{:d}:{}".format(item["appid"], item["name"]) for item in chunk]
                pipe.rpush("/SteamScraper/WorkerAppList", *chunk)

            await pipe.execute()




    '''
    '' If the flag "--populate-redis-steamapps" is used, request
    '' the Steam applist via HTTP API and populate queue '''
    async def populate_redis_with_steamapps(self):
        steamApps = await self.fetchAllSteamApps()

        if not steamApps:
            return

        # Update MySQL
        # It's important to do this before inserting to Redis, as
        # our scraper requires a database entry
        #! await self.insert_applist_to_mysql(steamApps)

        # Insert into Redis, this can be run async so that
        # this script can also start consuming tasks
        asyncio.ensure_future(self.insert_applist_to_redis(steamApps))