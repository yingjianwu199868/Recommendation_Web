package cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import entity.BytesObjectItem;
import entity.Item;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
/*
 * This is just a single instance of Redis
 */
public class SingleRedisConnection implements CacheConnection{
	
	private JedisPool jedisPool = null;
	
	public SingleRedisConnection() {
		try {
			//TODO: Need to do Better Config in the future.
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxTotal(RedisUtil.MAX_ACTIVE);
			
			jedisPool = new JedisPool(config,RedisUtil.HOSTNAME,RedisUtil.PORT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void invalidFavoriteCache(String userId) {
		// TODO Auto-generated method stub
		Jedis jedis = this.getJedis();
		jedis.del(userId.getBytes());
		this.closeJedis(jedis);
	}

	@Override
	public void setFavoriteCache(String userId, Set<Item> items) {
		Jedis jedis = this.getJedis();
		byte[] bytes = BytesObjectItem.serizlize(items);
		jedis.set(userId.getBytes(),bytes);
		this.closeJedis(jedis);
	}

	@Override
	public Set<Item> getFavoriteCache(String userId) {
		Set<Item> items = null;
		Jedis jedis = this.getJedis();
		byte[] bytes = jedis.get(userId.getBytes());
		if (bytes != null) {
			items = (Set<Item>)BytesObjectItem.deserialize(bytes);
		}
		this.closeJedis(jedis);
		return items;
	}

	@Override
	public void mockInsert() {
		// TODO Auto-generated method stub
		if (jedisPool == null) {
			System.out.println("Null");
		}
		Jedis jedis = this.getJedis();
		List<Integer> list = new ArrayList<>();
		list.add(1);
		byte[] bytes = BytesObjectItem.serizlize(list);
		jedis.set("abc".getBytes(),bytes);
		this.closeJedis(jedis);
		
	}

	@Override
	public void mockQuery() {
		// TODO Auto-generated method stub
		Jedis jedis = this.getJedis();
		byte[] bytes = jedis.get("abc".getBytes());
		if (bytes != null) {
			List<Integer> items = (List<Integer>)BytesObjectItem.deserialize(bytes);
			for (int i = 0; i < items.size(); i++) {
				System.out.println(items.get(i));
			}
		}
		this.closeJedis(jedis);
	}

	@Override
	public void mockDel() {
		// TODO Auto-generated method stub
		Jedis jedis = this.getJedis();
		jedis.del("abc".getBytes());
		this.closeJedis(jedis);
	}
	
	public Jedis getJedis() {
		Jedis jedis = this.jedisPool.getResource();
		return jedis;
	}
	
	public void closeJedis(Jedis jedis) {
		jedis.close();
	}

}
