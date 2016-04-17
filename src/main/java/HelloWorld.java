import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

public class HelloWorld {

    public static void main(String[] args) throws IOException, ParseException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        Directory index = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        // Adding Documents
        IndexWriter writer = new IndexWriter(index, config);
        addDoc(writer, "Lucene In Action", "12345");
        addDoc(writer, "Lucene For Dummies", "99999");
        addDoc(writer, "Introduction to Information retrieval", "11111");
        addDoc(writer, "The Art of Computer Programming ", "77777");
        writer.close();

        //Query
        //We read the query from stdin, parse it and build a lucene Query out of it.
        String queryString = args.length > 0 ? args[0] : "action";
        Query luceneQuery = new QueryParser("title", analyzer).parse(queryString);

        //Search
        //Using the Query we create a Searcher to search the index. Then a TopScoreDocCollector is instantiated
        // to collect the top 10 scoring hits.
        int numHits = 10;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(luceneQuery, numHits);
        ScoreDoc[] hits = docs.scoreDocs;

        //Display
        System.out.println("Found " + hits.length + " hits. ");
        for (int i = 0; i < hits.length; i++) {
            int documentId = hits[i].doc;
            Document document = searcher.doc(documentId);
            System.out.println((i + 1) + ". " + document.get("isbn") + " " + document.get("title"));
        }
    }

    /**
     * Add the document to the index
     *
     * @param writer
     * @param title
     * @param isbn
     * @throws IOException
     */
    private static void addDoc(IndexWriter writer, String title, String isbn) throws IOException {
        Document document = new Document();
        document.add(new TextField("title", title, Field.Store.YES));
        document.add(new StringField("isbn", isbn, Field.Store.YES));
        writer.addDocument(document);
    }


}
