CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    role ENUM('customer', 'admin', 'support') NOT NULL,
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
  ('customer', 'alice@aze.com', 'hashed_pw_1', 'Ali', 'Machin', 'fr', '1990-01-01'),
  ('customer', 'bob@aze.com', 'hashed_pw_2', 'Bob', 'Bidule', 'fr', '1980-01-01'),
  ('support', 'support@aze.com', 'hashed_pw_3', 'James', 'Bond', 'fr', '1970-01-01');

-- Insert chat messages
INSERT INTO chat_messages (sender_id, receiver_id, content, sent_at)
VALUES
  (1, 3, 'Lorem ipsum dolor sit amet.', '2025-08-20 10:00:00'),
  (3, 1, 'Consectetur adipiscing elit.', '2025-08-20 10:02:00'),
  (1, 3, 'Quis nostrud exercitation ullamco.', '2025-08-20 10:04:00'),
  (2, 3, 'Sed do eiusmod tempor.', '2025-08-20 10:06:00'),
  (3, 2, 'Ut enim ad minim veniam.', '2025-08-20 10:08:00'),
  (2, 3, 'Laboris nisi ut aliquip.', '2025-08-20 10:10:00');
