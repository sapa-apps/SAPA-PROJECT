const { v4: uuidv4 } = require('uuid');  // Pastikan uuidv4 diimpor
const { db } = require('../config/firebase-config');
const { Storage } = require('@google-cloud/storage');

const storage = new Storage({ keyFilename: 'Cloud-Storage.json' });
const bucketName = 'sapa-project';
const bucket = storage.bucket(bucketName);

exports.addContent = async (req, res) => {
  try {
    const { title, description } = req.body; 
    
    // Validasi input
    if (!title || !description || !req.file) {
      return res.status(400).json({ message: 'Title, description, and image file are required.' });
    }

    // Generate ID untuk konten
    const contentId = uuidv4();  // Generate content ID jika tidak ada yang diberikan

    // Nama file dengan path spesifik di bucket Cloud Storage
    const fileName = `content-images/${contentId}-${req.file.originalname}`;
    const file = bucket.file(fileName);

    // Unggah file ke Cloud Storage
    await file.save(req.file.buffer, {
      metadata: { contentType: req.file.mimetype },
    });

    // Membuat URL publik untuk file yang diunggah
    const publicUrl = `https://storage.googleapis.com/${bucketName}/${fileName}`;

    // Data yang akan disimpan di Firestore
    const contentData = {
      id: contentId,
      title,
      description,
      image: publicUrl, 
      createdAt: new Date().toISOString(),
    };

    // Simpan data ke Firestore
    await db.collection('contents').doc(contentId).set(contentData);

    return res.status(201).json({ message: 'Content added successfully', data: contentData });
  } catch (error) {
    console.error('Error adding content:', error);
    return res.status(500).json({ message: 'Internal Server Error', error: error.message });
  }
};

exports.getContents = async (req, res) => {
  try {
    // Mengambil semua konten dari Firestore
    const snapshot = await db.collection('contents').get();

    if (snapshot.empty) {
      return res.status(404).json({ message: 'No contents found.' });
    }

    // Mengubah snapshot Firestore menjadi array data konten
    const contents = snapshot.docs.map(doc => doc.data());

    return res.status(200).json({ message: 'Contents retrieved successfully', data: contents });
  } catch (error) {
    console.error('Error retrieving contents:', error);
    return res.status(500).json({ message: 'Internal Server Error', error: error.message });
  }
};

exports.getContentById = async (req, res) => {
  try {
    const { id } = req.params;  

    // Mencari konten berdasarkan ID
    const doc = await db.collection('contents').doc(id).get();

    if (!doc.exists) {
      return res.status(404).json({ message: 'Content not found.' });
    }

    return res.status(200).json({ message: 'Content retrieved successfully', data: doc.data() });
  } catch (error) {
    console.error('Error retrieving content:', error);
    return res.status(500).json({ message: 'Internal Server Error', error: error.message });
  }
};

exports.updateContent = async (req, res) => {
  try {
    const { id } = req.params;
    const { title, description } = req.body;

    // Validasi input
    if (!title && !description && !req.file) {
      return res.status(400).json({ message: 'At least one of title, description, or image file is required.' });
    }

    // Mencari konten berdasarkan ID
    const doc = await db.collection('contents').doc(id).get();

    if (!doc.exists) {
      return res.status(404).json({ message: 'Content not found.' });
    }

    const contentData = doc.data();

    // Update data jika ada perubahan
    if (title) contentData.title = title;
    if (description) contentData.description = description;

    // Jika ada file baru, unggah ke Cloud Storage
    if (req.file) {
      const contentId = contentData.id;
      const fileName = `content-images/${contentId}-${req.file.originalname}`;
      const file = bucket.file(fileName);

      await file.save(req.file.buffer, {
        metadata: { contentType: req.file.mimetype },
      });

      // Update URL gambar
      contentData.image = `https://storage.googleapis.com/${bucketName}/${fileName}`;
    }

    // Simpan data yang diperbarui ke Firestore
    await db.collection('contents').doc(id).set(contentData);

    return res.status(200).json({ message: 'Content updated successfully', data: contentData });
  } catch (error) {
    console.error('Error updating content:', error);
    return res.status(500).json({ message: 'Internal Server Error', error: error.message });
  }
};

exports.deleteContent = async (req, res) => {
  try {
    const { id } = req.params;

    // Pastikan tidak ada validasi body yang salah di DELETE
    // Cek apakah ID konten ada
    const doc = await db.collection('contents').doc(id).get();

    if (!doc.exists) {
      return res.status(404).json({ message: 'Content not found.' });
    }

    const contentData = doc.data();

    // Menghapus file dari Cloud Storage
    const fileName = contentData.image.split(`https://storage.googleapis.com/${bucketName}/`)[1];
    const file = bucket.file(fileName);

    await file.delete().catch(err => {
      console.error('Error deleting file from Cloud Storage:', err);
      throw new Error('Failed to delete file from Cloud Storage');
    });

    // Menghapus data dari Firestore
    await db.collection('contents').doc(id).delete();

    return res.status(200).json({ message: 'Content deleted successfully.' });
  } catch (error) {
    console.error('Error deleting content:', error);
    return res.status(500).json({ message: 'Internal Server Error', error: error.message });
  }
};
