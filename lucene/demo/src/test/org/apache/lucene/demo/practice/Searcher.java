/*
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
 */

package org.apache.lucene.demo.practice;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;

/**
 * Searcher
 *
 * @author lilei
 * @date 2021/12/9
 * @since 1.0.0
 */
public class Searcher {
    public static void search(String indexDir, String q) throws IOException, ParseException {
        // 3. 打开索引文件
        Directory dir = FSDirectory.open(new File(indexDir).toPath());
        DirectoryReader reader = DirectoryReader.open(dir);
        IndexSearcher is = new IndexSearcher(reader);
        // 4. 解析输入的查询字符串
        QueryParser parser = new QueryParser("contents", new StandardAnalyzer());
        Query query = parser.parse(q);
        long start = System.currentTimeMillis();
        // 5. 搜索索引
        TopDocs hits = is.search(query, 10);
        long end = System.currentTimeMillis();
        // 6. 记录搜索状态
        System.err.println("Found " + hits.totalHits +
                " document(s) (in " + (end - start) +
                " milliseconds) that matched query '" + q + "':");
        // 7. 返回匹配文本
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = is.doc(scoreDoc.doc);
            // 8. 显示匹配文件名
            System.out.println(doc.get("fullpath"));
        }
        // 9. 关闭 IndexSearcher
        reader.close();
    }

    public static void main(String[] args) throws IOException, ParseException {
        // 1. 解析输入的索引路径
        String indexDir = "/Users/lilei/Test/Lucene/Index";
        // 2. 解析输入的查询字符串
        String q = "beautiful girl";
        search(indexDir, q);
    }
}
