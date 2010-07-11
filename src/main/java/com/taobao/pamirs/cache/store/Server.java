package com.taobao.pamirs.cache.store;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.Promise;
import org.jgroups.util.Streamable;
import org.jgroups.util.Util;

public class Server extends ReceiverAdapter implements Master, Slave, Serializable
{
	/**
     * 
     */
	private static final long	                  serialVersionUID	 = 1L;
	private String	                              props	             = "tcp.xml";
	private Channel	                              ch;

	/** Maps task IDs to Tasks */
	private final ConcurrentMap<ClusterID, Entry>	tasks	         = new ConcurrentHashMap<ClusterID, Entry>();

	/** Used to handle received tasks */
	private final ExecutorService	              thread_pool	     = Executors.newCachedThreadPool();

	private View	                              view;
	private int	                                  rank	             = -1;
	private int	                                  cluster_size	     = -1;
	private MessageDispatcher	                  dispatcher;
	private RpcDispatcher	                      rpcDispatcher;
	private final boolean	                      propagateException	= false;

	public Server(final String props)
	{
		this.props = props;

	}

	public Server(final Channel channel) throws ChannelException
	{
		ch = channel;

		ch.setReceiver(this);
		ch.connect("PamirsTunnel");
		rpcDispatcher = new RpcDispatcher(ch, null, null, this);
	}

	public void start() throws Exception
	{

		ch = new JChannel(props);

		ch.setReceiver(this);
		ch.connect("PamirsTunnel");
		dispatcher = new MessageDispatcher(ch, null, null, new RequestHandler()
		{

			public Object handle(final Message msg)
			{
				System.out.println("got a message: " + msg);
				if (propagateException)
				{
					throw new RuntimeException("failed to handle message: " + msg.getObject());
				} else
				{
					return new String("success");
				}
			}

		});
		rpcDispatcher = new RpcDispatcher(ch, null, null, this);
		log(ch.getInfo() + "");
	}

	public void callRemoteMethods(final String methodName, final Object[] values, final Class[] clazz)
	{
		rpcDispatcher.callRemoteMethods(null, methodName, values, clazz, org.jgroups.blocks.Request.GET_ALL,
		        0);
	}

	public void stop()
	{
		thread_pool.shutdown();
		ch.close();
		dispatcher.stop();
		rpcDispatcher.stop();
	}

	public String info()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("local_addr=" + ch.getLocalAddress() + "\nview=" + view).append("\n");
		sb.append("rank=" + rank + "\n");
		sb.append("(" + tasks.size() + " entries in tasks cache)");
		return sb.toString();
	}

	public Object submit(final Task task, final long timeout) throws Exception
	{
		final ClusterID id = ClusterID.create(ch.getLocalAddress());
		Object result = null;
		try
		{
			final Request req = new Request(Request.Type.EXECUTE, task, id, null);
			final byte[] buf = Utils.streamableToByteBuffer(req);
			final Entry entry = new Entry(task, ch.getLocalAddress());
			tasks.put(id, entry);
			log("==> submitting " + id);
			ch.send(new Message(null, null, buf));
			// wait on entry for result
			result = entry.promise.getResultWithTimeout(timeout);
		} catch (final Exception ex)
		{
			tasks.remove(id); // remove it again
			throw ex;
		}
		if (result instanceof Exception)
		{
			throw (Exception) result;
		}
		return result;
	}

	public Object handle(final Task task)
	{
		return task.execute();
	}

	/** All we receive is Requests */
	@Override
	public void receive(final Message msg)
	{
		try
		{
			final Request req = (Request) Util.streamableFromByteBuffer(Request.class, msg.getRawBuffer(),
			        msg.getOffset(), msg.getLength());
			switch (req.type)
			{
			case EXECUTE:
				handleExecute(req.id, msg.getSrc(), req.task);
				break;
			case RESULT:
				final Entry entry = tasks.get(req.id);
				if (entry == null)
				{
					err("found no entry for request " + req.id);
				} else
				{
					entry.promise.setResult(req.result);
				}
				multicastRemoveRequest(req.id);
				break;
			case REMOVE:
				tasks.remove(req.id);
				break;
			default:
				throw new IllegalArgumentException("type " + req.type + " is not recognized");
			}
		} catch (final Exception e)
		{
			err("exception receiving message from " + msg.getSrc(), e);
		}
	}

	private void multicastRemoveRequest(final ClusterID id)
	{
		final Request remove_req = new Request(Request.Type.REMOVE, null, id, null);
		try
		{
			final byte[] buf = Util.streamableToByteBuffer(remove_req);
			ch.send(new Message(null, null, buf));
		} catch (final Exception e)
		{
			err("failed multicasting REMOVE request", e);
		}
	}

	private void handleExecute(final ClusterID id, final Address sender, final Task task)
	{
		tasks.putIfAbsent(id, new Entry(task, sender));
		final int index = id.getId() % cluster_size;
		if (index != rank)
		{
			return;
		}

		// System.out.println("ID=" + id + ", index=" + index + ", my rank=" +
		// rank);
		execute(id, sender, task);
	}

	private void execute(final ClusterID id, final Address sender, final Task task)
	{
		final Handler handler = new Handler(id, sender, task);
		thread_pool.execute(handler);
	}

	@Override
	public void viewAccepted(final View view)
	{
		final List<Address> left_members = Utils.leftMembers(this.view, view);
		this.view = view;
		final Address local_addr = ch.getLocalAddress();
		System.out.println("view: " + view);
		cluster_size = view.size();
		final Vector<Address> mbrs = view.getMembers();
		final int old_rank = rank;
		for (int i = 0; i < mbrs.size(); i++)
		{
			final Address tmp = mbrs.get(i);
			if (tmp.equals(local_addr))
			{
				rank = i;
				break;
			}
		}
		if ((old_rank == -1) || (old_rank != rank))
		{
			log("my rank is " + rank);
		}

		// process tasks by left members
		if ((left_members != null) && !left_members.isEmpty())
		{
			for (final Address mbr : left_members)
			{
				handleLeftMember(mbr);
			}
		}
	}

	/**
	 * Take over the tasks previously assigned to this member *if* the ID
	 * matches my (new rank)
	 */
	private void handleLeftMember(final Address mbr)
	{
		for (final Map.Entry<ClusterID, Entry> entry : tasks.entrySet())
		{
			final ClusterID id = entry.getKey();
			final int index = id.getId() % cluster_size;
			if (index != rank)
			{
				return;
			}
			final Entry val = entry.getValue();
			if (mbr.equals(val.submitter))
			{
				err("will not take over tasks submitted by " + mbr + " because it left the cluster");
				continue;
			}
			log("**** taking over task " + id + " from " + mbr + " (submitted by " + val.submitter + ")");
			execute(id, val.submitter, val.task);
		}
	}

	public static void main(final String[] args) throws Exception
	{
		final String props = "t.xml";

		final Server server = new Server(props);
		server.start();

		loop(server);
	}

	private static void loop(final Server server)
	{
		boolean looping = true;
		while (looping)
		{
			final int key = Util.keyPress("[1] Submit [2] Submit long running task [3] Info [q] Quit");
			Utils.discardUntilNewLine(System.in);
			switch (key)
			{
			case '1':
				Task task = new Task()
				{
					private static final long	serialVersionUID	= 5102426397394071700L;

					public Object execute()
					{
						return new Date();
					}
				};
				_submit(task, server);
				break;
			case '2':
				task = new Task()
				{
					private static final long	serialVersionUID	= 5102426397394071700L;

					public Object execute()
					{
						System.out.println("sleeping for 15 secs...");
						Util.sleep(15000);
						System.out.println("done");
						return new Date();
					}
				};
				_submit(task, server);
				break;
			case '3':
				System.out.println(server.info());
				break;
			case 'q':
				looping = false;
				break;
			case 'r':
				break;
			case '\n':
				break;
			case -1:
				looping = false;
				break;
			}
		}
		server.stop();
	}

	private static void _submit(final Task task, final Server server)
	{
		try
		{
			final Object result = server.submit(task, 30000);
			log("<== result = " + result);
		} catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

	private static void help()
	{
		System.out.println("Server [-props <JGroups properties>]");
	}

	private static void log(final String msg)
	{
		System.out.println(msg);
	}

	private static void err(final String msg)
	{
		System.err.println(msg);
	}

	private static void err(final String msg, final Throwable t)
	{
		System.err.println(msg + ", ex=" + t);
	}

	private static class Entry
	{
		private final Task		      task;
		private final Address		  submitter;
		private final Promise<Object>	promise	= new Promise<Object>();

		public Entry(final Task task, final Address submitter)
		{
			this.task = task;
			this.submitter = submitter;
		}
	}

	private class Handler implements Runnable
	{
		final ClusterID	id;
		final Address	sender;
		final Task		task;

		public Handler(final ClusterID id, final Address sender, final Task task)
		{
			this.id = id;
			this.sender = sender;
			this.task = task;
		}

		public void run()
		{
			Object result = null;
			if (task != null)
			{
				try
				{
					log("executing " + id);
					result = handle(task);
				} catch (final Throwable t)
				{
					err("failed executing " + id, t);
					result = t;
				}
			}
			final Request response = new Request(Request.Type.RESULT, null, id, result);
			try
			{
				final byte[] buf = Util.streamableToByteBuffer(response);
				final Message rsp = new Message(sender, null, buf);
				ch.send(rsp);
			} catch (final Exception e)
			{
				err("failed executing task " + id, e);
			}
		}
	}

	public static class Request implements Streamable
	{
		static enum Type
		{
			EXECUTE, RESULT, REMOVE
		};

		private Type		type;
		private Task		task;
		private ClusterID	id;
		private Object		result;

		public Request()
		{
		}

		public Request(final Type type, final Task task, final ClusterID id, final Object result)
		{
			this.type = type;
			this.task = task;
			this.id = id;
			this.result = result;
		}

		public void writeTo(final DataOutputStream out) throws IOException
		{
			out.writeInt(type.ordinal());
			try
			{
				Utils.objectToStream(task, out);
			} catch (final Exception e)
			{
				final IOException ex = new IOException("failed marshalling of task " + task);
				ex.initCause(e);
				throw ex;
			}
			Util.writeStreamable(id, out);
			try
			{
				Utils.objectToStream(result, out);
			} catch (final Exception e)
			{
				final IOException ex = new IOException("failed to marshall result object");
				ex.initCause(e);
				throw ex;
			}
		}

		public void readFrom(final DataInputStream in) throws IOException, IllegalAccessException,
		        InstantiationException
		{
			final int tmp = in.readInt();
			switch (tmp)
			{
			case 0:
				type = Type.EXECUTE;
				break;
			case 1:
				type = Type.RESULT;
				break;
			case 2:
				type = Type.REMOVE;
				break;
			default:
				throw new InstantiationException("ordinal " + tmp + " cannot be mapped to enum");
			}
			try
			{
				task = (Task) Utils.objectFromStream(in);
			} catch (final Exception e)
			{
				final InstantiationException ex = new InstantiationException(
				        "failed reading task from stream");
				ex.initCause(e);
				throw ex;
			}
			id = (ClusterID) Util.readStreamable(ClusterID.class, in);
			try
			{
				result = Utils.objectFromStream(in);
			} catch (final Exception e)
			{
				final IOException ex = new IOException("failed to unmarshal result object");
				ex.initCause(e);
				throw ex;
			}
		}

	}

	public int invokeMethod(final String props)
	{
		try
		{
			System.out.println("Ö´ÐÐ: " + props);
			// final Object obj =
			// AppContext.getApplicationContext().getBean("applicationServiceImpl");
			// if (obj != null)
			// {
			// obj.getClass().getMethod("initializeAllDeclaredConsumerBeans").invoke(obj);
			// }

		} catch (final Exception e)
		{
			System.err.println("Error: " + e.getMessage());
		}
		return 1;
	}

}