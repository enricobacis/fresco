/*
 * Copyright (c) 2015, 2016 FRESCO (http://github.com/aicis/fresco).
 *
 * This file is part of the FRESCO project.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * FRESCO uses SCAPI - http://crypto.biu.ac.il/SCAPI, Crypto++, Miracl, NTL,
 * and Bouncy Castle. Please see these projects for any further licensing issues.
 *******************************************************************************/
package dk.alexandra.fresco.framework.sce.configuration;

import dk.alexandra.fresco.framework.Party;
import dk.alexandra.fresco.framework.ProtocolEvaluator;
import dk.alexandra.fresco.framework.configuration.NetworkConfiguration;
import dk.alexandra.fresco.framework.network.NetworkingStrategy;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.framework.sce.resources.storage.Storage;
import dk.alexandra.fresco.framework.sce.resources.storage.StreamedStorage;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class TestSCEConfiguration<ResourcePoolT extends ResourcePool> implements SCEConfiguration {

  private final ProtocolSuiteConfiguration<ResourcePoolT> suite;
  private NetworkingStrategy network;
  private Storage storage;
  private Map<Integer, Party> parties;
  private int myId;
  private ProtocolEvaluator evaluator;

  public TestSCEConfiguration(ProtocolSuiteConfiguration<ResourcePoolT> suite,
      NetworkingStrategy network,
      ProtocolEvaluator evaluator,
      NetworkConfiguration conf, Storage storage,
      boolean useSecureConn) {
    this(suite, network, evaluator, conf, storage, useSecureConn, 4096);

  }

  public TestSCEConfiguration(ProtocolSuiteConfiguration<ResourcePoolT> suite,
      NetworkingStrategy network,
      ProtocolEvaluator evaluator,
      NetworkConfiguration conf, Storage storage, boolean useSecureConn, int maxBatchSize) {
    this.suite = suite;
    this.network = network;
    this.storage = storage;
    this.evaluator = evaluator;
    evaluator.setMaxBatchSize(maxBatchSize);
    this.myId = conf.getMyId();
    parties = new HashMap<>();
    for (int i = 1; i <= conf.noOfParties(); i++) {
      if (useSecureConn) {
        Party p = conf.getParty(i);
        //Use the same hardcoded test 128 bit AES key for all connections
        p.setSecretSharedKey("w+1qn2ooNMCN7am9YmYQFQ==");
        parties.put(i, p);
      } else {
        parties.put(i, conf.getParty(i));
      }
    }
  }

  @Override
  public int getMyId() {
    return myId;
  }

  @Override
  public Map<Integer, Party> getParties() {
    return parties;
  }

  @Override
  public Level getLogLevel() {
    return Level.INFO;
  }

  @Override
  public ProtocolEvaluator getEvaluator() {
    return this.evaluator;
  }

  @Override
  public StreamedStorage getStreamedStorage() {
    if (this.storage instanceof StreamedStorage) {
      return (StreamedStorage) this.storage;
    } else {
      return null;
    }
  }

  public ProtocolSuiteConfiguration<ResourcePoolT> getSuite() {
    return suite;
  }

  @Override
  public NetworkingStrategy getNetworkStrategy() {
    return this.network;
  }

}
