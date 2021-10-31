--liquibase formatted sql

--changeset rrmasgutov:init

create table account1
(
    id bigserial constraint account_pk primary key,
    amount int,
    version int
);

--changeset adobrynin:addValues
insert into account1 values
(1, 500, 1),
(2, 500, 1);
insert into account1 values
(3, 800, 1);

--changeset adobrynin:addIndex
CREATE INDEX index_account ON account1 USING HASH (id);

--changeset adobrynin:add integrity constraint
ALTER TABLE public.account1
    ADD CONSTRAINT amount CHECK (amount>=0)
    NOT VALID;




