/*
 * This file is generated by jOOQ.
 */
package com.ssafy.jooq;


import com.ssafy.jooq.tables.Flupet;
import com.ssafy.jooq.tables.FoodType;
import com.ssafy.jooq.tables.MemberFlupet;
import com.ssafy.jooq.tables.records.FlupetRecord;
import com.ssafy.jooq.tables.records.FoodTypeRecord;
import com.ssafy.jooq.tables.records.MemberFlupetRecord;

import org.jooq.ForeignKey;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in
 * fluffit_flupet.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<FlupetRecord> KEY_FLUPET_PRIMARY = Internal.createUniqueKey(Flupet.FLUPET, DSL.name("KEY_flupet_PRIMARY"), new TableField[] { Flupet.FLUPET.ID }, true);
    public static final UniqueKey<FoodTypeRecord> KEY_FOOD_TYPE_PRIMARY = Internal.createUniqueKey(FoodType.FOOD_TYPE, DSL.name("KEY_food_type_PRIMARY"), new TableField[] { FoodType.FOOD_TYPE.ID }, true);
    public static final UniqueKey<MemberFlupetRecord> KEY_MEMBER_FLUPET_PRIMARY = Internal.createUniqueKey(MemberFlupet.MEMBER_FLUPET, DSL.name("KEY_member_flupet_PRIMARY"), new TableField[] { MemberFlupet.MEMBER_FLUPET.ID }, true);

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<MemberFlupetRecord, FlupetRecord> FK_MEMBER_FLUPET_FLUPET_ID = Internal.createForeignKey(MemberFlupet.MEMBER_FLUPET, DSL.name("fk_member_flupet_flupet_id"), new TableField[] { MemberFlupet.MEMBER_FLUPET.FLUPET_ID }, Keys.KEY_FLUPET_PRIMARY, new TableField[] { Flupet.FLUPET.ID }, true);
}