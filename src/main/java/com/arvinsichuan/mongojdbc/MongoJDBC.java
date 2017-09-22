package com.arvinsichuan.mongojdbc;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.util.Scanner;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

/**
 * @Project TwentySeventeenAutumn
 * @Author arvinsc@foxmail.com
 * @Date 2017/9/21
 * @Package com.arvinsichuan.mongojdbc
 */
public class MongoJDBC {
    public static void main(String[] args) {
        Scanner scanner=new Scanner(System.in);
        MongoClientURI uri = new MongoClientURI(
                "mongodb://Dawn:"+scanner.next()+"@cluster0-shard-00-00-rwqvv.mongodb.net:27017," +
                        "cluster0-shard-00-01-rwqvv.mongodb.net:27017," +
                        "cluster0-shard-00-02-rwqvv.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin");
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase("experiments");
        MongoCollection<Document> docCollection=database.getCollection("experiments");
        FindIterable<Document> iterable=docCollection.find();
        MongoCursor<Document> cursor=iterable.iterator();
        System.out.println("************************");
        while (cursor.hasNext()){
            System.out.println(cursor.next());
        }
        Document document=new Document("id", UUID.randomUUID().toString().replace("-",""));
        document.append("name","Arvin");
        docCollection.insertOne(document);
        JsonObjectBuilder objectBuilder=Json.createObjectBuilder();

        BasicDBObject dbObject=new BasicDBObject("_id",new  ObjectId("59bbc95888d7c1a3d42f2a23"));


        for (Document cur :
                docCollection.find()) {
            System.out.println(cur.toJson());
        }




        docCollection.updateOne(dbObject,new Document("$set",new Document("Mood","Fine")));

        for (Document cur :
                docCollection.find()) {
            System.out.println(cur.toJson());
        }

    }
}
