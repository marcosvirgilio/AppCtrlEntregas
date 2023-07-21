create table aluno (
    id integer AUTO_INCREMENT not null,
    matricula varchar(14) not null,
    nome varchar(255) not null,
    PRIMARY KEY (id));

create table protocolo (
    id BIGINT AUTO_INCREMENT not null,
    matricula varchar(14) not null,
    data date not null,
    PRIMARY KEY (id));