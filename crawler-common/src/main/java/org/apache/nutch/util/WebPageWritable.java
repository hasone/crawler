/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.apache.nutch.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.Writable;
import org.apache.nutch.storage.WebPage;
import org.apache.gora.util.IOUtils;

public class WebPageWritable extends Configured
implements Writable {

  private WebPageMy webPage;

  public WebPageWritable() {
    this(null, new WebPageMy());
  }

  public WebPageWritable(Configuration conf, WebPageMy webPage) {
    super(conf);
    this.webPage = webPage;
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    webPage = IOUtils.deserialize(getConf(), in, webPage, WebPageMy.class);
  }

  @Override
  public void write(DataOutput out) throws IOException {
    IOUtils.serialize(getConf(), out, webPage, WebPageMy.class);
  }

  public WebPageMy getWebPage() {
    return webPage;
  }
  
  public void setWebPage(WebPageMy webPage) {
    this.webPage = webPage;
  }

}
