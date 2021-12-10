package org.apache.lucene.demo.practice;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;

/**
 * Indexer
 *
 * @author lilei
 * @date 2021/12/9
 * @since 1.0.0
 */
public class Indexer {

    private final IndexWriter writer;

    public Indexer(String indexDir) throws IOException {
        // 3. 创建 Lucene Index Writer
        Directory dir = FSDirectory.open(new File(indexDir).toPath());
        writer = new IndexWriter(dir, new IndexWriterConfig(new StandardAnalyzer()));
    }

    public void close() throws IOException {
        // 4. 关闭 Index Writer
        writer.close();
    }

    public int index(String dataDir, FileFilter filter) throws IOException {
        File[] files = new File(dataDir).listFiles();
        if (files != null) {
            for (File f : files) {
                if (!f.isDirectory() &&
                        !f.isHidden() &&
                        f.exists() &&
                        f.canRead() &&
                        (filter == null || filter.accept(f))) {
                    indexFile(f);
                }
            }
        }
        // 5. 返回被索引文档数
        return writer.getDocStats().numDocs;
    }

    private static class TextFilesFilter implements FileFilter {
        @Override
        public boolean accept(File path) {
            // 6. 只索引.txt文件，采用FileFilter
            return path.getName().toLowerCase().endsWith(".txt");
        }
    }

    protected Document getDocument(File f) throws IOException {
        Document doc = new Document();
        // 7. 索引文件内容
        doc.add(new TextField("contents", new FileReader(f)));
        // 8. 索引文件名称
        doc.add(new StringField("filename", f.getName(), Field.Store.YES));
        // 9. 索引文件
        doc.add(new StringField("fullpath", f.getCanonicalPath(), Field.Store.YES));
        return doc;
    }

    private void indexFile(File file) throws IOException {
        System.out.println("Indexing " + file.getCanonicalPath());
        Document doc = getDocument(file);
        // 10. 向Lucene索引中添加文档
        writer.addDocument(doc);
    }

    public static void main(String[] args) throws IOException {
        // 1. 在指定目录创建索引
        String indexDir = "/Users/lilei/Test/Lucene/Index";
        // 2. 对指定目录中的*.txt文件进行索引
        String dataDir = "/Users/lilei/Test/Lucene/Data";
        long start = System.currentTimeMillis();
        Indexer indexer = new Indexer(indexDir);
        int numIndexed;
        try {
            numIndexed = indexer.index(dataDir, new TextFilesFilter());
        } finally {
            indexer.close();
        }
        long end = System.currentTimeMillis();
        System.out.println("Indexing " + numIndexed + " files took " + (end - start) + " milliseconds");
    }
}
