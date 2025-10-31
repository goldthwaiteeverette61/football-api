docker run -d --name redis -p 6379:6379 -v ./:/usr/local/etc/redis redis redis-server /usr/local/etc/redis/redis.conf
