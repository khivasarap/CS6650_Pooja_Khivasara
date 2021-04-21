import com.rabbitmq.client.Channel;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.NoSuchElementException;

public class ChannelPool implements Cloneable {
  private GenericObjectPool<Channel> internalPool;
  public static GenericObjectPoolConfig defaultConfig;

  static {
    defaultConfig = new GenericObjectPoolConfig();
    defaultConfig.setMaxTotal(10000);
    defaultConfig.setBlockWhenExhausted(false);
  }

  public ChannelPool() throws ChannelException {
    this(defaultConfig, new ChannelFactory());
  }
  public ChannelPool(String hostname, String queueName) throws ChannelException {
    this(defaultConfig, new ChannelFactory(hostname,  queueName));
  }

  public ChannelPool(final GenericObjectPoolConfig poolConfig, ChannelFactory factory) {
    if (this.internalPool != null) {
      try {
        closeInternalPool();
      } catch (Exception e) {
      }
    }

    this.internalPool = new GenericObjectPool<Channel>(factory, poolConfig);
  }

  private void closeInternalPool() throws ChannelException {
    try {
      internalPool.close();
    } catch (Exception e) {
      throw new ChannelException("Could not destroy the pool", e);
    }
  }

  public void returnChannel(Channel channel) throws ChannelException {
    try {
      if (channel.isOpen()) {
        internalPool.returnObject(channel);
      } else {
        internalPool.invalidateObject(channel);
      }
    } catch (Exception e) {
      throw new ChannelException("Could not return the resource to the pool", e);
    }
  }

  public Channel getChannel() throws ChannelException {
    try {
      return internalPool.borrowObject();
    } catch (NoSuchElementException nse) {
      if (null == nse.getCause()) { // The exception was caused by an exhausted pool
        throw new ChannelException("Could not get a resource since the pool is exhausted", nse);
      }
      // Otherwise, the exception was caused by the implemented activateObject() or ValidateObject()
      throw new ChannelException("Could not get a resource from the pool", nse);
    } catch (Exception e) {
      throw new ChannelException("Could not get a resource from the pool", e);
    }
  }
}