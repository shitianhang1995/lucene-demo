package com.sth;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.File;

/**
 * @Auther: root
 * @Date: 2019/7/27 13:04
 * @Description:
 */
public class LuceneFirst {
    @Test
    public void createIndex() throws Exception {
        Directory directory = FSDirectory.open(new File("F:\\index").toPath());

        IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig());
        File dir = new File("F:\\searchsource");
        File[] files = dir.listFiles();
        for (File f : files) {
            String fileName = f.getName();
            String filePath = f.getPath();
            String fileContent = FileUtils.readFileToString(f, "utf8");
            long fileSize = FileUtils.sizeOf(f);
            TextField fieldName = new TextField("name", fileName, Field.Store.YES);
            TextField fieldPath = new TextField("path", filePath, Field.Store.YES);
            TextField fieldContent = new TextField("content", fileContent, Field.Store.YES);
            TextField fieldSize = new TextField("size", fileSize + "", Field.Store.YES);
            //创建文档对象
            Document document = new Document();
            document.add(fieldName);
            document.add(fieldPath);
            document.add(fieldContent);
            document.add(fieldSize);
            //把文档对象写入索引库
            indexWriter.addDocument(document);
        }
        indexWriter.close();
    }

    @Test
    public void searahIndex() throws Exception {
        Directory directory = FSDirectory.open(new File("F:\\index").toPath());

        DirectoryReader indexReader = DirectoryReader.open(directory);

        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        Query query = new TermQuery(new Term("content", "spring"));

        TopDocs topDocs = indexSearcher.search(query, 10);

        System.out.println("查询总记录数:" + topDocs.totalHits);

        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        for (ScoreDoc doc : scoreDocs) {
            int docId = doc.doc;
            Document document = indexSearcher.doc(docId);
            System.out.println(document.get("name"));
            System.out.println(document.get("path"));
            System.out.println(document.get("size"));
            System.out.println(document.get("content"));
            System.out.println("--------------分割线---------------------");
        }
        indexReader.close();
    }
}
