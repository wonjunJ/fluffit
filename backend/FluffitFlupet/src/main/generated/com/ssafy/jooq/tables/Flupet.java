/*
 * This file is generated by jOOQ.
 */
package com.ssafy.jooq.tables;


import com.ssafy.jooq.FlupetService;
import com.ssafy.jooq.Keys;
import com.ssafy.jooq.tables.records.FlupetRecord;

import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function4;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row4;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.jooq.types.UInteger;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Flupet extends TableImpl<FlupetRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>flupet_service.flupet</code>
     */
    public static final Flupet FLUPET = new Flupet();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<FlupetRecord> getRecordType() {
        return FlupetRecord.class;
    }

    /**
     * The column <code>flupet_service.flupet.id</code>.
     */
    public final TableField<FlupetRecord, UInteger> ID = createField(DSL.name("id"), SQLDataType.INTEGERUNSIGNED.nullable(false).identity(true), this, "");

    /**
     * The column <code>flupet_service.flupet.name</code>.
     */
    public final TableField<FlupetRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(10).nullable(false), this, "");

    /**
     * The column <code>flupet_service.flupet.img_url</code>.
     */
    public final TableField<FlupetRecord, String> IMG_URL = createField(DSL.name("img_url"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>flupet_service.flupet.stage</code>.
     */
    public final TableField<FlupetRecord, Integer> STAGE = createField(DSL.name("stage"), SQLDataType.INTEGER.nullable(false), this, "");

    private Flupet(Name alias, Table<FlupetRecord> aliased) {
        this(alias, aliased, null);
    }

    private Flupet(Name alias, Table<FlupetRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>flupet_service.flupet</code> table reference
     */
    public Flupet(String alias) {
        this(DSL.name(alias), FLUPET);
    }

    /**
     * Create an aliased <code>flupet_service.flupet</code> table reference
     */
    public Flupet(Name alias) {
        this(alias, FLUPET);
    }

    /**
     * Create a <code>flupet_service.flupet</code> table reference
     */
    public Flupet() {
        this(DSL.name("flupet"), null);
    }

    public <O extends Record> Flupet(Table<O> child, ForeignKey<O, FlupetRecord> key) {
        super(child, key, FLUPET);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : FlupetService.FLUPET_SERVICE;
    }

    @Override
    public Identity<FlupetRecord, UInteger> getIdentity() {
        return (Identity<FlupetRecord, UInteger>) super.getIdentity();
    }

    @Override
    public UniqueKey<FlupetRecord> getPrimaryKey() {
        return Keys.KEY_FLUPET_PRIMARY;
    }

    @Override
    public Flupet as(String alias) {
        return new Flupet(DSL.name(alias), this);
    }

    @Override
    public Flupet as(Name alias) {
        return new Flupet(alias, this);
    }

    @Override
    public Flupet as(Table<?> alias) {
        return new Flupet(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Flupet rename(String name) {
        return new Flupet(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Flupet rename(Name name) {
        return new Flupet(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Flupet rename(Table<?> name) {
        return new Flupet(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<UInteger, String, String, Integer> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function4<? super UInteger, ? super String, ? super String, ? super Integer, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function4<? super UInteger, ? super String, ? super String, ? super Integer, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}