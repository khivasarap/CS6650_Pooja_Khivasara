use databasePooja;
DROP TABLE IF EXISTS Purchase;

CREATE TABLE Test (
num int);

TRUNCATE Purchase;

CREATE TABLE Purchase (
Id int auto_increment,
	StoreId int,
    CustomerId int,
    Date VARCHAR(255),
    body json,
	CONSTRAINT pk_PurchaseItems PRIMARY KEY (Id)
);

select * from Purchase;

select count(*) from Purchase;