package com.example;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.internal.InternalUtils;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.model.*;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;

@Singleton
public class Repository {
    private AmazonDynamoDB client;

    @Value("${dynamodb.key}")
    private String dbUser;

    @Value("${dynamodb.secret}")
    private String dbSecret;

    public Repository() {
        //System.out.println("dbuser: " + dbUser + "\tdbsecret: " + dbSecret);
        BasicAWSCredentials provider = new BasicAWSCredentials("AKIA23DWV2JOW274C4OB", "qcsPdL71C+y97/IglYEfac1k1qNsr7CBjGjAmWDY");

        client = AmazonDynamoDBClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(provider)).withRegion(Regions.EU_NORTH_1).build();

    }

    public void saveData(Country country) {
        HashMap<String, AttributeValue> item_values =
                new HashMap<String, AttributeValue>();

        item_values.put("country", new AttributeValue(country.getName()));
        item_values.put("rank", new AttributeValue(country.getRank()));
        item_values.put("population", new AttributeValue().withN(String.valueOf(country.getPopulation())));
        item_values.put("ratio", new AttributeValue(String.valueOf(country.getRatio())));

        try {
            client.putItem("Hackit22MicronautLabCountry", item_values);
        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The table \"%s\" can't be found.\n", "Hackit22MicronautLabCountry");
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
            System.exit(1);
        } catch (AmazonServiceException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }


    }

    public Mono<Country> getByCountry(String h) {
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("Hackit22MicronautLabCountry");
        GetItemSpec spec = new GetItemSpec()
                .withPrimaryKey("country", h);
        Item country = table.getItem(spec);
        List<Country> l = new ArrayList<>();
        l.add(new Country((String) country.get("name"), (String) country.get("rank"), BigDecimal.valueOf((Long) country.get("population")), Double.parseDouble((String) country.get("ratio"))));
        Flux<Country> f = Flux.fromIterable(l);


        System.out.println(country);
        return f.last();
    }


    public Flux<Country> readDynamo() {

        Flux<Country> f = Flux.fromIterable(readDynamoAsList());
        return f;
    }

    public List<Country> readDynamoAsList() {
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("Hackit22MicronautLabCountry");
        ItemCollection<ScanOutcome> c = table.scan();
        List<Country> l = new ArrayList<>();
        c.forEach(v -> l.add(new Country((String) v.get("name"), (String) v.get("rank"), (BigDecimal) v.get("population"), Double.parseDouble((String) v.get("ratio")))));
        return l;
    }


    public Flux<Country> getCountriesByPopulation(String population) {
        System.out.println("population: " + population);
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("Hackit22MicronautLabCountry");
        /*
        Map<String, Object> expressionAttributeValues = new HashMap<String, Object>();
        expressionAttributeValues.put(":pr", 100);
        ItemCollection<ScanOutcome> items = table.scan (
                "country = :pr",                                  //FilterExpression
                "country",     //ProjectionExpression
                null,                                           //No ExpressionAttributeNames
                expressionAttributeValues);

        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next().toJSONPretty());
        }*/
        Map<String, AttributeValue> expressionAttributeValues =
                new HashMap<String, AttributeValue>();
        expressionAttributeValues.put(":val", new AttributeValue().withN(population));
        System.out.println(Arrays.stream(expressionAttributeValues.keySet().toArray()).toList());
        ScanRequest scanRequest = new ScanRequest()
                .withTableName("Hackit22MicronautLabCountry")
                .withFilterExpression("population > :val")
                .withProjectionExpression("country, population")
                .withExpressionAttributeValues(expressionAttributeValues);
        ;
        ScanResult result = client.scan(scanRequest);

        List<Country> l = new ArrayList<>();
        result.getItems().forEach(v -> l.add(new Country(v.get("country").getS(), new BigDecimal(v.get("population").getN()))));

        Flux<Country> f = Flux.fromIterable(l);
        //return result.getItems();

        System.out.println(result.getItems().get(0));
        for (Map<String, AttributeValue> item : result.getItems()) {
            System.out.println(item);
        }
        return f;


    }

    public void eraseDynamo() {
        DynamoDB dynamoDB = new DynamoDB(client);


        Table table = dynamoDB.getTable("Hackit22MicronautLabCountry");
        List <Country> hej = readDynamo().collectList().block(Duration.ofMillis(15000));

        hej.forEach(country->{
            System.out.println("HEEEEEj");
            System.out.println(country.getName());
            DeleteItemOutcome outcome = table.deleteItem("country", country.getName());

        });



    }

    private DeleteItemOutcome doDeleteItem(DeleteItemSpec spec, String tableName) {
        DynamoDB dynamoDB = new DynamoDB(client);


        // set up the keys
        DeleteItemRequest req = spec.getRequest().withTableName(tableName)
                .withKey(InternalUtils.toAttributeValueMap(spec.getKeyComponents()));
        // set up the expected attribute map, if any
        final Collection<Expected> expected = spec.getExpected();
        final Map<String, ExpectedAttributeValue> expectedMap =
                InternalUtils.toExpectedAttributeValueMap(expected);
        // set up the value map, if any (when expression API is used)
        final Map<String, AttributeValue> attrValMap =
                InternalUtils.fromSimpleMap(spec.getValueMap());
        // set up the request
        req.withExpected(expectedMap)
                .withExpressionAttributeNames(spec.getNameMap())
                .withExpressionAttributeValues(attrValMap)
        ;
        DeleteItemResult result = client.deleteItem(req);
        return new DeleteItemOutcome(result);
    }
}
