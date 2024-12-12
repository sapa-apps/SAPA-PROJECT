const admin = require('firebase-admin'); 
const  {db, auth} = require('../config/firebase-config');
const { Storage } = require('@google-cloud/storage');

// Inisialisasi Google Cloud Storage
const storage = new Storage({ keyFilename: 'Cloud-Storage.json' });
const bucketName = 'sapa-project';
const bucket = storage.bucket(bucketName);

exports.historyTranslation = async (req, res) => {
  try {
    const { translation, capture } = req.body;
    const userId = req.user.uid || "anonymous"; // This is the UID from Firebase Auth (can be anonymous or authenticated)

    if (!translation || !capture) {
      return res.status(400).json({ error: "Translation and capture are required" });
    }

    // Validasi dan decode Base64 image (pastikan capture berupa string Base64)
    const captureData = capture.split(",")[1]; // Mengambil data Base64 setelah comma
    if (!captureData) {
      return res.status(400).json({ error: "Invalid image capture" });
    }

   const buffer = Buffer.from(capture.split(",")[1], "base64");

// Menentukan format file berdasarkan ekstensi (JPEG atau PNG)
const fileExtension = capture.startsWith("data:image/png") ? "png" : "jpeg";
const filePath = `translation-input/${userId}/${Date.now()}.${fileExtension}`;  // Dinamis, tergantung format
const file = bucket.file(filePath);

// Menyimpan file dengan contentType yang sesuai
await file.save(buffer, {
  metadata: { contentType: `image/${fileExtension}` },  // Menyesuaikan contentType dengan format
});

const imageUrl = `https://storage.googleapis.com/${bucket.name}/${filePath}`;
    await db.collection("translations").add({
      userId,
      translation,
      imageUrl,
      timestamp: admin.firestore.FieldValue.serverTimestamp(),
    });

    res.status(200).json({ message: "Translation with capture saved successfully!" });
  } catch (error) {
    console.error("Error saving translation with capture:", error);
    res.status(500).json({ error: "Failed to save translation with capture" });
  }
};

