
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    role ENUM('CUSTOMER', 'ADMIN', 'SUPPORT') NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    language VARCHAR(50),
    date_of_birth DATE
);

CREATE TABLE chat_messages (
    chat_message_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    content TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL DEFAULT NULL,
    FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Insert users
INSERT INTO users (role, email, password, first_name, last_name, language, date_of_birth)
VALUES
  ('customer', 'ali@aze.com', 'hashed_pw_1', 'Ali', 'Machin', 'fr', '1990-01-01'),
  ('customer', 'bob@aze.com', 'hashed_pw_2', 'Bob', 'Bidule', 'fr', '1980-01-01'),
  ('support', 'support@aze.com', 'hashed_pw_3', 'Jean', 'Truc', 'fr', '1970-01-01');

-- Insert chat messages
INSERT INTO chat_messages (sender_id, receiver_id, content, sent_at)
VALUES
  (1, 3, 'Bonjour, j''ai un problème avec ma voiture.', '2025-08-20 10:00:00'),
  (3, 1, 'Bonjour, expliquez-moi le problème s''il vous plait.', '2025-08-20 10:02:00'),
  (1, 3, 'Elle ne démarre pas.', '2025-08-20 10:04:00'),
  (2, 3, 'Bonsoir, c''est le service client ?', '2025-08-20 10:06:00'),
  (3, 2, 'Bonsoir, oui je fais partie du support YourCarYourWay, en quoi puis-je vous aider ?', '2025-08-20 10:08:00'),
  (2, 3, 'D''abord, êtes vous un humain ou un robot ?', '2025-08-20 10:10:00');
