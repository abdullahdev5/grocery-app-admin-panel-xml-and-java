# GroceryApp — Android Grocery App (XML + Java) + Admin Panel

**GroceryApp** is a complete Android grocery shopping application built with **XML layouts** and **Java**, plus a companion **Admin Panel Android app** for managing products and orders. The app uses Firebase (Authentication, Firestore, Cloud Storage) and integrates a **Gemini API**-based chatbot.

---

## 🚀 Key Features

### 🛒 User App
- **Firebase Email & Password Authentication** with profile picture upload (Firebase Cloud Storage)
- Home screen showing products from Firestore
- **Product Detail Page** — description, images, add to cart, add to wishlist
- **Search Products** by name and category
- **Cart Management** — add, increase/decrease quantity, delete
- **Wishlist** — save favorite products
- **Orders** — All Orders screen with details
- **Order Status Tracking** — e.g., Pending → Delivered
- **Reviews System** — To Review page (pending reviews), My Reviews page
- **Chatbot** powered by **Gemini API**
- **WhatsApp Contact** for quick support

### 🛠 Admin Panel App
- Admin login
- **Upload / Edit / Delete Products** (image upload to Firebase Storage, data to Firestore)
- **Update Order Status** for user orders
- Manage categories and product details

---

## 📱 Screens / Flow
- Login / Signup (with profile picture)
- Home (product listing)
- Search Results
- Product Details
- Cart & Checkout
- Orders & Order Status
- To Review / My Reviews
- Chatbot & WhatsApp Support
- Profile Screen

---

## 🗄 Firestore Collections (Example)
- **Users/{userId}**
- **Products/{productId}**
- **Orders/{orderId}**
- **Reviews/{reviewId}**
- **Users/{userId}/Carts/{productId}**
- **Users/{userId}/Wishlists/{productId}**

---

## 💻 Tech Stack
- **Android**: XML layouts + Java
- **Backend**: Firebase Authentication, Firestore, Cloud Storage
- **Chatbot**: Gemini API
- **IDE**: Android Studio
- **Build Tool**: Gradle

---

## Screenshots

![screenshot_1](https://github.com/user-attachments/assets/2293bc82-da1e-4a15-a4b4-32761b74680a)

![screenshot_2](https://github.com/user-attachments/assets/24a7d89c-1467-436b-8084-3dd3f6412b3d)

![screenshot_3](https://github.com/user-attachments/assets/7c71a616-e2ce-4516-8104-ea9ffa521bc4)

![screenshot_4](https://github.com/user-attachments/assets/c55bca9e-8d19-4d7c-af6a-e88c6a8aedbd)

![screenshot_5](https://github.com/user-attachments/assets/d946e316-c27b-45f5-a0ee-ec63c5ca3245)

![screenshot_6](https://github.com/user-attachments/assets/2d1a6b07-2821-46c1-8112-90363d4e592d)

![screenshot_7](https://github.com/user-attachments/assets/f4acec27-a498-420b-8e1b-879e5f4d6cd5)

![screenshot_8](https://github.com/user-attachments/assets/3e05697d-a30a-4c0e-9e8f-967318db055d)

![screenshot_9](https://github.com/user-attachments/assets/ebcb78ff-b25c-4b5f-8866-5c160e874a99)


---


## 🛠 Setup Instructions
1. Clone the repository  
   ```bash
   git clone https://github.com/abdullahdev5/grocery-app-admin-panel-xml-and-java.git
