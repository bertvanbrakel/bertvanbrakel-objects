package com.bertvanbrakel.objects;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;

import org.junit.Test;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.iterator.ORecordIteratorClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class OrientDbTest {

    @Test
    public void play1() {

        final OGraphDatabase db = Orient.instance().getDatabaseFactory().createGraphDatabase("local:/tmp/orientdb_test" + System.currentTimeMillis());
        db.create();
        db.begin();

        db.commit();
        db.close();

    }

    @Test
    public void play2_insert() throws Exception {
    	for(int run = 0; run < 5;run++){
            final ODatabaseDocumentTx db = new ODatabaseDocumentTx("local:/tmp/orientdb_databases/petshop" + System.currentTimeMillis());
            db.create();
            new ODocument("Person").save();
            final int NUM = 100000;
            final int NUM_FIELDS = 20;
            
            try {
                //db.getMetadata().getSchema().createClass( "ORIDs" );
              //  db.begin(TXTYPE.OPTIMISTIC);
            	{
                	int count = 0;
                	final long start = System.currentTimeMillis();
                    for (int i = 0; i < NUM; i++) {
                        Collection<String> col = newArrayList();
                    	for( int j = 0; j < NUM_FIELDS;j++){            		
                        	col.add("fieldValue" + i + "." + j);
                        }
                        count += col.size();
                    }
                    final long end = System.currentTimeMillis();
                    printStats(start,end,NUM,"baseline");
                    System.out.println(count + " num fields");
            	}
                {
                    final long start = System.currentTimeMillis();
                    for (int i = 0; i < NUM; i++) {
                        final ODocument person = new ODocument("Person");
                        person.field("name", "Bob" + i);
                        for( int j = 0; j < NUM_FIELDS;j++){
                        	person.field("field" + j, "fieldValue" + i + "." + j);
                        }
                        //person.
                        // animal.
                        person.save();
                    }
                    final long end = System.currentTimeMillis();
                    printStats(start,end,NUM,"insert");
                }
              //lets walk the lot
                {
                    final long start = System.currentTimeMillis();
                    ORecordIteratorClass<ODocument> people = db.browseClass("Person");
                    for(ODocument person:people){
                    	person.fieldNames();
                    }
                    final long end = System.currentTimeMillis();
                    printStats(start,end,NUM,"walk");
                 }
                //lets walk the lot and modify
                {
                    final long start = System.currentTimeMillis();
                    int c = 0;
                    ORecordIteratorClass<ODocument> people = db.browseClass("Person");
                    for(ODocument person:people){
                    	person.field("field-add1",c);
                    	person.field("field-add2",c);
                    	person.field("field-add3",c);
                    	person.field("field-add4",c);
                    	
                    	person.save();
                    }
                    final long end = System.currentTimeMillis();
                    printStats(start,end,NUM,"mod");
                 }
                
                //db.commit();
            } catch (final Exception e){
                //db.rollback();
                throw e;
            } finally {
                db.close();
            }
    	}
    }

    private void printStats(long start,long end,int num, String label){
        System.out.println(label +":took " + (end - start) + "ms for " + num + " records");
        System.out.println(label + ":" + (end - start)*1000/num + "us/record");
    	
    }
    @Test
    public void play2_relationships() throws Exception {
        final ODatabaseDocumentTx db = new ODatabaseDocumentTx("local:/tmp/orientdb_databases/petshop" + System.currentTimeMillis());
        db.create();
        final int NUM = 100000;
        try {
            //db.getMetadata().getSchema().createClass( "ORIDs" );
          //  db.begin(TXTYPE.OPTIMISTIC);

            final long start = System.currentTimeMillis();
            for (int i = 0; i < NUM; i++) {
                final ODocument person = new ODocument("Person");
                person.field("name", "Fred" + i);
                person.field("location", "Madrid" + i);
                // animal.
                person.save();
            }
            final long end = System.currentTimeMillis();
            System.out.println("took:" + (end - start) + "ms");
            System.out.println((end - start)/NUM + "ms/record");
            //now connect ppl
            try {
                final ORecordIteratorClass<ODocument> docs = db.browseClass("Person");
                for( final ODocument doc:docs){

                }
                db.commit();
            } catch (final Exception e){
                db.rollback();
            }
            //db.commit();
        } catch (final Exception e){
            //db.rollback();
            throw e;
        } finally {
            db.close();
        }
    }


}
