set names utf8mb4;
set charset utf8mb4;

drop database if exists luigi;
create database luigi charset utf8mb4;
use luigi;

create table pizzas (
  id int unsigned not null auto_increment primary key,
  naam varchar(50) not null,
  prijs decimal(10,2) not null,
  winst decimal(10,2)
);

insert into pizzas(naam,prijs,winst) values 
('Bianca', 4, 0.4),
('Capriciosa', 4.5, 0.6),
('Prosciutto', 4, 0.5),
('Margheritta', 5, 0.7),
('Calzone', 4, 0.3),
('Fungi & Olive', 5, 0.8),
('Quattro formaggi', 5.5, 1),
('Quattro stagioni', 5.6, 1.2),
('Viennese', 5.2, 0.9),
('Napoletana', 4.5, 0.6);

create table prijzen (
	pizzaId int unsigned not null,
    prijs decimal(10,2) not null,
    vanaf datetime not null,
    constraint prijzenPizzas foreign key(pizzaId) references pizzas(id) on delete cascade
);

insert into prijzen
select id as pizzaId, prijs, now() as vanaf
from pizzas;

create user if not exists cursist identified by 'cursist';
grant select,insert,update,delete on pizzas to cursist;
grant select,insert on prijzen to cursist;