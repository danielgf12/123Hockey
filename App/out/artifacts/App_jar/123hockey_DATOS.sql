DROP DATABASE IF EXISTS 123hockey_test;
CREATE DATABASE 123hockey_test;
USE 123hockey_test;

DROP DATABASE IF EXISTS 123hockey_test;
CREATE DATABASE 123hockey_test;
USE 123hockey_test;

-- Tables (copied from 123HockeyBBDD.txt, slightly adapted for clarity)
CREATE TABLE Usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    apellidos VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    telefono VARCHAR(20),
    contrasena VARCHAR(255),
    rol ENUM('ENTRENADOR', 'DELEGADO', 'JUGADOR') NOT NULL,
    usuario VARCHAR(50) UNIQUE,
    fotoUsuario LONGBLOB,
    club VARCHAR(50),
    ciudad VARCHAR(20)
);

CREATE TABLE Jugador_Info (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT,
    fecha_nacimiento DATE,
    telefono_padres VARCHAR(20),
    posicion ENUM('PORTERO', 'JUGADOR'),
    categoria VARCHAR(50),
    asistencia_obligatoria INT DEFAULT 0,
    asistencia_voluntaria INT DEFAULT 0,
    partidos_jugados INT DEFAULT 0,
    descripcion TEXT,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id)
);

CREATE TABLE Equipo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    liga VARCHAR(20),
    categoria VARCHAR(20),
    club VARCHAR(100),
    ciudad VARCHAR(100),
    pais VARCHAR(100),
    fotoEquipo LONGBLOB
);

CREATE TABLE Equipo_Jugador (
    id_equipo INT,
    id_usuario INT,
    PRIMARY KEY (id_equipo, id_usuario),
    FOREIGN KEY (id_equipo) REFERENCES Equipo(id),
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id)
);

CREATE TABLE Entrenamiento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_equipo INT,
    fecha DATETIME,
    ubicacion VARCHAR(255),
    repetir ENUM('NINGUNO', 'SEMANAL', 'QUINCENAL'),
    tipoEntrenamiento ENUM('PISTA', 'FISICO', 'TACTICA'),
    observaciones TEXT,
    FOREIGN KEY (id_equipo) REFERENCES Equipo(id)
);

CREATE TABLE Asistencia (
    id_entrenamiento INT,
    id_usuario INT,
    asistencia BOOLEAN,
    PRIMARY KEY (id_entrenamiento, id_usuario),
    FOREIGN KEY (id_entrenamiento) REFERENCES Entrenamiento(id),
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id)
);

CREATE TABLE Partido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_equipo INT,
    rival VARCHAR(100),
    lugar VARCHAR(100),
    fecha DATETIME,
    tipoPartido ENUM('AMISTOSO', 'LIGA', 'TORNEO', 'CAMPEONATO'),
    info TEXT,
    horaQuedada TIME,
    FOREIGN KEY (id_equipo) REFERENCES Equipo(id)
);

CREATE TABLE Alineacion (
    id_partido INT,
    id_usuario INT,
    PRIMARY KEY (id_partido, id_usuario),
    FOREIGN KEY (id_partido) REFERENCES Partido(id),
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id)
);


INSERT INTO Usuario (nombre, apellidos, email, telefono, contrasena, rol, usuario, club, ciudad) VALUES
('Domingo', 'Martí', 'entrenador@demo.com', '600000001', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'ENTRENADOR', 'entrenador01', 'Zaragoza', 'Zaragoza');
INSERT INTO Usuario (nombre, apellidos, email, telefono, contrasena, rol, usuario, club, ciudad) VALUES
('Rubén', 'Terrón', 'delegado1@demo.com', '600000011', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'DELEGADO', 'delegado1', 'Zaragoza', 'Zaragoza'),
('Fabián', 'Ledesma', 'delegado2@demo.com', '600000012', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'DELEGADO', 'delegado2', 'Zaragoza', 'Zaragoza'),
('Rogelio', 'Aguado', 'delegado3@demo.com', '600000013', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'DELEGADO', 'delegado3', 'Zaragoza', 'Zaragoza');
INSERT INTO Equipo (nombre, liga, categoria, club, ciudad, pais) VALUES
('Zaragoza Alevín', 'Liga A', 'ALEVÍN', 'Zaragoza', 'Zaragoza', 'España'),
('Zaragoza Infantil', 'Liga B', 'INFANTIL', 'Zaragoza', 'Zaragoza', 'España'),
('Zaragoza Juvenil', 'Liga C', 'JUVENIL', 'Zaragoza', 'Zaragoza', 'España');
INSERT INTO Usuario (nombre, apellidos, email, telefono, contrasena, rol, usuario, club, ciudad) VALUES
('Conrado', 'Vendrell', 'conrado.vendrell0@demo.com', '600270126', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'JUGADOR', 'conve1', 'Zaragoza', 'Zaragoza'),
('Valeria', 'Garzón', 'valeria.garzón1@demo.com', '600283151', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'JUGADOR', 'valga2', 'Zaragoza', 'Zaragoza'),
('Violeta', 'Sancho', 'violeta.sancho2@demo.com', '600972867', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'JUGADOR', 'viosa3', 'Zaragoza', 'Zaragoza'),
('Eusebia', 'Madrigal', 'eusebia.madrigal3@demo.com', '600678934', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'JUGADOR', 'eusma4', 'Zaragoza', 'Zaragoza'),
('Paula', 'Aramburu', 'paula.aramburu4@demo.com', '600698570', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'JUGADOR', 'pauar5', 'Zaragoza', 'Zaragoza'),
('Ceferino', 'Pérez', 'ceferino.pérez5@demo.com', '600586902', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'JUGADOR', 'cefpé6', 'Zaragoza', 'Zaragoza'),
('Susana', 'Asenjo', 'susana.asenjo6@demo.com', '600949529', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'JUGADOR', 'susas7', 'Zaragoza', 'Zaragoza'),
('Luciana', 'Vicente', 'luciana.vicente7@demo.com', '600654142', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'JUGADOR', 'lucvi8', 'Zaragoza', 'Zaragoza'),
('Ezequiel', 'Lucas', 'ezequiel.lucas8@demo.com', '600157460', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'JUGADOR', 'ezelu9', 'Zaragoza', 'Zaragoza'),
('Teresa', 'Lorenzo', 'teresa.lorenzo9@demo.com', '600433714', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'JUGADOR', 'terlo10', 'Zaragoza', 'Zaragoza'),
('Dorotea', 'Noguera', 'dorotea.noguera10@demo.com', '600701588', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'JUGADOR', 'dorno11', 'Zaragoza', 'Zaragoza'),
('Vinicio', 'Simó', 'vinicio.simó11@demo.com', '600819993', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'JUGADOR', 'vinsi12', 'Zaragoza', 'Zaragoza'),
('Calisto', 'Julián', 'calisto.julián12@demo.com', '600832317', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'JUGADOR', 'calju13', 'Zaragoza', 'Zaragoza'),
('Plácido', 'Durán', 'plácido.durán13@demo.com', '600865768', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'JUGADOR', 'pládu14', 'Zaragoza', 'Zaragoza'),
('Eva María', 'Larrañaga', 'eva maría.larrañaga14@demo.com', '600777061', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'JUGADOR', 'evala15', 'Zaragoza', 'Zaragoza'),
('Valentina', 'Múgica', 'valentina.múgica15@demo.com', '600689637', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'JUGADOR', 'valmú16', 'Zaragoza', 'Zaragoza'),
('Leticia', 'Cid', 'leticia.cid16@demo.com', '600529144', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'JUGADOR', 'letci17', 'Zaragoza', 'Zaragoza'),
('Alejo', 'Cámara', 'alejo.cámara17@demo.com', '600366234', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'JUGADOR', 'alecá18', 'Zaragoza', 'Zaragoza'),
('Obdulia', 'Valenzuela', 'obdulia.valenzuela18@demo.com', '600725104', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'JUGADOR', 'obdva19', 'Zaragoza', 'Zaragoza'),
('Eladio', 'Prado', 'eladio.prado19@demo.com', '600954126', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'JUGADOR', 'elapr20', 'Zaragoza', 'Zaragoza'),
('Celestina', 'Sales', 'celestina.sales20@demo.com', '600910806', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'JUGADOR', 'celsa21', 'Zaragoza', 'Zaragoza'),
('Olimpia', 'Saez', 'olimpia.saez21@demo.com', '600403190', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'JUGADOR', 'olisa22', 'Zaragoza', 'Zaragoza'),
('Julieta', 'Montserrat', 'julieta.montserrat22@demo.com', '600594674', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'JUGADOR', 'julmo23', 'Zaragoza', 'Zaragoza'),
('Marcelo', 'Infante', 'marcelo.infante23@demo.com', '600593349', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'JUGADOR', 'marin24', 'Zaragoza', 'Zaragoza'),
('Luciano', 'Perera', 'luciano.perera24@demo.com', '600275796', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'JUGADOR', 'lucpe25', 'Zaragoza', 'Zaragoza');
INSERT INTO Jugador_Info (id_usuario, fecha_nacimiento, telefono_padres, posicion, categoria, descripcion) VALUES
(5, '2007-07-15', '+34708 96 19 16', 'PORTERO', 'JUVENIL', 'Reprehenderit cum distinctio nemo quidem.'),
(6, '2007-09-14', '+34734 36 58 16', 'JUGADOR', 'ALEVÍN', 'Sunt maxime consectetur ipsum dolores.'),
(7, '2007-06-13', '+34882580647', 'PORTERO', 'JUVENIL', 'Eos cumque quae error laboriosam tempore.'),
(8, '2007-01-25', '+34942 723 045', 'JUGADOR', 'SENIOR', 'Assumenda accusantium sint laboriosam alias doloribus.'),
(9, '2003-02-12', '+34712 539 709', 'PORTERO', 'ALEVÍN', 'Rerum aliquam dolor culpa.'),
(10, '2005-04-16', '+34 727 98 35 36', 'JUGADOR', 'INFANTIL', 'Adipisci asperiores labore sequi aspernatur.'),
(11, '2008-05-18', '+34704 492 593', 'JUGADOR', 'SENIOR', 'Praesentium vitae sint iure incidunt perspiciatis.'),
(12, '2008-09-21', '+34649194952', 'PORTERO', 'JUVENIL', 'Quidem qui voluptatibus quae consectetur ducimus pariatur.'),
(13, '2008-01-09', '+34 703 082 185', 'PORTERO', 'ALEVÍN', 'Perferendis error laboriosam ullam beatae.'),
(14, '2008-05-16', '+34728 395 253', 'PORTERO', 'SENIOR', 'Nemo mollitia sequi cum sed.'),
(15, '2008-02-19', '+34740 77 42 07', 'JUGADOR', 'SENIOR', 'Libero ab omnis occaecati iusto.'),
(16, '2006-01-09', '+34610 950 438', 'JUGADOR', 'INFANTIL', 'Esse ea quibusdam est doloremque.'),
(17, '2006-05-13', '+34 665291955', 'PORTERO', 'INFANTIL', 'Culpa minus facilis quisquam accusantium.'),
(18, '2009-07-11', '+34747168738', 'PORTERO', 'ALEVÍN', 'Dolorem nemo temporibus eum.'),
(19, '2008-06-06', '+34 811 549 261', 'PORTERO', 'INFANTIL', 'Numquam molestias repellat tempora facere tempora laboriosam.'),
(20, '2005-04-03', '+34 704676809', 'PORTERO', 'SENIOR', 'Impedit at repellendus.'),
(21, '2009-01-22', '+34639 91 79 24', 'PORTERO', 'JUVENIL', 'Tempore nobis cumque ratione doloremque alias tempore.'),
(22, '2009-07-26', '+34633 86 18 81', 'JUGADOR', 'INFANTIL', 'Voluptates modi quis voluptas magni id nesciunt commodi.'),
(23, '2005-06-13', '+34719 26 91 04', 'PORTERO', 'ALEVÍN', 'Rerum rem minima tenetur.'),
(24, '2007-12-03', '+34924076624', 'JUGADOR', 'INFANTIL', 'Neque debitis eius.'),
(25, '2003-07-04', '+34737665863', 'PORTERO', 'JUVENIL', 'Impedit molestiae vero earum vero doloremque laudantium.'),
(26, '2003-06-19', '+34724 74 16 55', 'JUGADOR', 'SENIOR', 'Dolor ea quibusdam natus in dolore eum.'),
(27, '2007-12-06', '+34 734 420 651', 'PORTERO', 'JUVENIL', 'Tempore consequatur eius vero culpa.'),
(28, '2007-04-03', '+34716 05 55 91', 'PORTERO', 'SENIOR', 'Facere repudiandae voluptas numquam.'),
(29, '2007-12-17', '+34600 675 604', 'JUGADOR', 'SENIOR', 'Amet rerum explicabo consequatur veniam tenetur amet.');
INSERT INTO Equipo_Jugador (id_equipo, id_usuario) VALUES
(3, 5),
(1, 6),
(3, 7),
(3, 8),
(1, 9),
(2, 10),
(3, 11),
(3, 12),
(1, 13),
(1, 14),
(1, 15),
(2, 16),
(2, 17),
(1, 18),
(2, 19),
(2, 20),
(3, 21),
(2, 22),
(1, 23),
(2, 24),
(3, 25),
(3, 26),
(3, 27),
(2, 28),
(2, 29);
INSERT INTO Entrenamiento (id_equipo, fecha, ubicacion, repetir, tipoEntrenamiento, observaciones) VALUES
(1, '2025-06-02 18:00:00', 'Pista 1', 'NINGUNO', 'PISTA', 'Ejercicio 1'),
(2, '2025-06-03 18:00:00', 'Pista 2', 'NINGUNO', 'PISTA', 'Ejercicio 2'),
(3, '2025-06-04 18:00:00', 'Pista 3', 'NINGUNO', 'PISTA', 'Ejercicio 3'),
(1, '2025-06-05 18:00:00', 'Pista 4', 'NINGUNO', 'PISTA', 'Ejercicio 4');
INSERT INTO Asistencia (id_entrenamiento, id_usuario, asistencia) VALUES
(1, 5, TRUE),
(1, 6, FALSE),
(1, 7, TRUE),
(1, 8, TRUE),
(1, 9, TRUE),
(1, 10, FALSE),
(1, 11, FALSE),
(1, 12, FALSE),
(1, 13, FALSE),
(1, 14, FALSE),
(1, 15, TRUE),
(1, 16, FALSE),
(1, 17, TRUE),
(1, 18, FALSE),
(1, 19, TRUE),
(1, 20, TRUE),
(1, 21, FALSE),
(1, 22, FALSE),
(1, 23, TRUE),
(1, 24, FALSE),
(1, 25, FALSE),
(1, 26, TRUE),
(1, 27, TRUE),
(1, 28, FALSE),
(1, 29, TRUE),
(2, 5, FALSE),
(2, 6, TRUE),
(2, 7, FALSE),
(2, 8, FALSE),
(2, 9, FALSE),
(2, 10, TRUE),
(2, 11, FALSE),
(2, 12, TRUE),
(2, 13, FALSE),
(2, 14, FALSE),
(2, 15, FALSE),
(2, 16, TRUE),
(2, 17, FALSE),
(2, 18, FALSE),
(2, 19, FALSE),
(2, 20, TRUE),
(2, 21, TRUE),
(2, 22, TRUE),
(2, 23, FALSE),
(2, 24, FALSE),
(2, 25, TRUE),
(2, 26, TRUE),
(2, 27, FALSE),
(2, 28, TRUE),
(2, 29, TRUE),
(3, 5, TRUE),
(3, 6, FALSE),
(3, 7, TRUE),
(3, 8, FALSE),
(3, 9, TRUE),
(3, 10, FALSE),
(3, 11, FALSE),
(3, 12, TRUE),
(3, 13, TRUE),
(3, 14, FALSE),
(3, 15, FALSE),
(3, 16, FALSE),
(3, 17, FALSE),
(3, 18, FALSE),
(3, 19, FALSE),
(3, 20, FALSE),
(3, 21, FALSE),
(3, 22, TRUE),
(3, 23, TRUE),
(3, 24, TRUE),
(3, 25, FALSE),
(3, 26, FALSE),
(3, 27, TRUE),
(3, 28, FALSE),
(3, 29, TRUE),
(4, 5, TRUE),
(4, 6, TRUE),
(4, 7, TRUE),
(4, 8, TRUE),
(4, 9, FALSE),
(4, 10, FALSE),
(4, 11, FALSE),
(4, 12, TRUE),
(4, 13, TRUE),
(4, 14, FALSE),
(4, 15, TRUE),
(4, 16, FALSE),
(4, 17, FALSE),
(4, 18, FALSE),
(4, 19, FALSE),
(4, 20, TRUE),
(4, 21, FALSE),
(4, 22, TRUE),
(4, 23, TRUE),
(4, 24, TRUE),
(4, 25, FALSE),
(4, 26, FALSE),
(4, 27, FALSE),
(4, 28, TRUE),
(4, 29, FALSE);
INSERT INTO Partido (id_equipo, rival, lugar, fecha, tipoPartido, info, horaQuedada) VALUES
(1, 'Tres Cantos', 'Madrid', '2025-06-10 10:00:00', 'LIGA', 'Partido importante', '09:00:00'),
(2, 'Premia', 'Barcelona', '2025-06-15 11:30:00', 'TORNEO', 'Segundo encuentro', '10:15:00');
INSERT INTO Alineacion (id_partido, id_usuario) VALUES
(1, 5),
(1, 6),
(1, 7),
(1, 8),
(1, 9),
(1, 10),
(1, 11),
(1, 12),
(1, 13),
(1, 14),
(1, 15),
(1, 16),
(1, 17),
(2, 5),
(2, 6),
(2, 7),
(2, 8),
(2, 9),
(2, 10),
(2, 11),
(2, 12),
(2, 13),
(2, 14),
(2, 15),
(2, 16),
(2, 17);