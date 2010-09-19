package com.taobao.pamirs.sync.protocol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.taobao.pamirs.sync.protocol.DefaultProtocol;
import com.taobao.pamirs.sync.replicator.data.CacheConfigData;

public class TCPProtocolBuilderTest extends TestCase
{

    public void testTCPPingBuilder() throws IOException
    {
        final List<CacheConfigData> ccd = new ArrayList<CacheConfigData>();
        String tcp = DefaultProtocol.TCP.split(":")[0];
        tcp = tcp.replaceAll("TCP\\(", "");
        tcp = tcp.replaceAll("\\)", "");
        final String[] pairs = tcp.split(";");

        for (final String pair : pairs)
        {
            final CacheConfigData cd = new CacheConfigData();
            final String key = pair.split("=")[0];
            cd.setConfigKey(key.trim());
            if (pair.split("=").length > 1)
            {
                final String value = pair.split("=")[1];
                cd.setConfigValue(value.trim());
            }
            ccd.add(cd);
        }

        System.out.println(ccd);

    }

    public void testDiscoveryProtocol() throws IOException
    {
        final List<CacheConfigData> ccd = new ArrayList<CacheConfigData>();
        String tcp = DefaultProtocol.TCP.split(":")[1];
        tcp = tcp.replaceAll("TCPPING\\(", "");
        tcp = tcp.replaceAll("\\)", "");
        final String[] pairs = tcp.split(";");

        for (final String pair : pairs)
        {
            final CacheConfigData cd = new CacheConfigData();
            final String key = pair.split("=")[0];
            cd.setConfigKey(key.trim());
            if (pair.split("=").length > 1)
            {
                final String value = pair.split("=")[1];
                cd.setConfigValue(value.trim());
            }
            ccd.add(cd);
        }

        System.out.println(ccd);

    }
}
