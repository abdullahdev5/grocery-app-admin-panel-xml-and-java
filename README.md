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

## 🛠 Setup Instructions
1. Clone the repository  
   ```bash
   git clone https://github.com/abdullahdev5/grocery-app-admin-panel-xml-and-java.git
