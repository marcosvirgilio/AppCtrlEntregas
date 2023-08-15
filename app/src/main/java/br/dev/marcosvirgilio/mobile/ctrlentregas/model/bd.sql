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

ALTER TABLE protocolo ADD UNIQUE matricula (matricula, data);


    Update aluno as a inner
    join ( select matricula, qrcode
          from base_testes_id_estudantes___alunos
    	where qrcode is not NULL) as qr
    on a.matricula = qr.matricula
	set a.qrcode = qr.qrcode;
