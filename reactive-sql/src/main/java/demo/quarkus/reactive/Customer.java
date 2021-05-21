package demo.quarkus.reactive;

import io.smallrye.mutiny.Multi;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

public class Customer {
    private Long id;
    private String name;
    private String surname;

    public Customer(Long id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }
    public Customer( ) {

    }
    @Override
    public String toString() {
        return id+","+name+","+surname;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }


    public static Multi<Customer> findAll(PgPool client) {
        return client.query("SELECT id, name, surname FROM CUSTOMER ORDER BY name ASC").execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(Customer::from);
    }

    public static Uni<Customer> findById(PgPool client, Long id) {
        return client.preparedQuery("SELECT id, name, surname FROM CUSTOMER WHERE id = $1").execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
    }

    public Uni<Long> create(PgPool client) {
        return client.preparedQuery("INSERT INTO CUSTOMER (id, name, surname) VALUES (nextval('customerId_seq'), $1,$2) RETURNING id").execute(Tuple.of(name, surname))
                .onItem().transform(pgRowSet -> pgRowSet.iterator().next().getLong("id"));
    }

    public Uni<Boolean> update(PgPool client) {
        return client.preparedQuery("UPDATE CUSTOMER SET name = $1, surname = $2 WHERE id = $3").execute(Tuple.of(name, surname, id))
                .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    public static Uni<Boolean> delete(PgPool client, Long id) {
        return client.preparedQuery("DELETE FROM CUSTOMER WHERE id = $1").execute(Tuple.of(id))
                .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    private static Customer from(Row row) {
        return new Customer(row.getLong("id"), row.getString("name"), row.getString("surname"));
    }
}