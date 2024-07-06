package com.bayesserver.util;

import com.bayesserver.Network;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * 贝叶斯工具类
 *
 * @author lijing
 * @date 2024-07-06
 */
public class BayesUtil {



    public static void saveBayes(String fileName) throws XMLStreamException, IOException
    {
        Network network = new Network();
        network.save(fileName);
    }
}
