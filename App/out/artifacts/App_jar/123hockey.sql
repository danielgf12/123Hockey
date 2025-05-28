CREATE DATABASE IF NOT EXISTS 123hockey;
USE 123hockey;

-- Tabla de usuarios
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

-- Datos complementarios para jugadores
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

-- Equipos
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

-- Relación Jugador - Equipo
CREATE TABLE Equipo_Jugador (
    id_equipo INT,
    id_usuario INT,
    PRIMARY KEY (id_equipo, id_usuario),
    FOREIGN KEY (id_equipo) REFERENCES Equipo(id),
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id)
);

-- Entrenamientos
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

-- Asistencia de jugadores a entrenamientos
CREATE TABLE Asistencia (
    id_entrenamiento INT,
    id_usuario INT,
    asistencia BOOLEAN,
    PRIMARY KEY (id_entrenamiento, id_usuario),
    FOREIGN KEY (id_entrenamiento) REFERENCES Entrenamiento(id),
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id)
);

-- Partidos
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

-- Alineación de jugadores para partidos
CREATE TABLE Alineacion (
    id_partido INT,
    id_usuario INT,
    PRIMARY KEY (id_partido, id_usuario),
    FOREIGN KEY (id_partido) REFERENCES Partido(id),
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id)
);
