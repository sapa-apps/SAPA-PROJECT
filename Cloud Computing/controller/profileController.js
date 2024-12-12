const admin = require('firebase-admin'); 
const  {db, auth} = require('../config/firebase-config');
const { Storage } = require('@google-cloud/storage');

// Inisialisasi Google Cloud Storage
const storage = new Storage({ keyFilename: 'Cloud-Storage.json' });
const bucketName = 'sapa-project';
const bucket = storage.bucket(bucketName);

// Mendapatkan profil pengguna
exports.getProfile = async (req, res) => {
  const uid = req.user.uid; 
  try {
    const userRef = db.collection('users').doc(uid);
    const userDoc = await userRef.get();

    if (!userDoc.exists) {
      return res.status(404).json({ error: 'Pengguna tidak ditemukan' });
    }

    const userData = userDoc.data();
    res.status(200).json({
      nama: userData.nama,
      email: userData.email,
      fotoProfil: userData.fotoProfil || null,
    });
  } catch (error) {
    console.error(`Error saat mendapatkan profil untuk email ${email}:`, error);
    res.status(500).json({ error: 'Gagal mendapatkan profil pengguna' });
  }
};

// Mengubah profil pengguna
exports.updateProfile = async (req, res) => {
  const uid = req.user.uid; // Gunakan UID untuk referensi dokumen
  const { nama, emailBaru, passwordBaru } = req.body;

  // Validasi email baru
  if (emailBaru && !/^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/.test(emailBaru)) {
    return res.status(400).json({ error: 'Email tidak valid' });
  }

  const userRef = db.collection('users').doc(uid);

  try {
    const userDoc = await userRef.get(); // Ambil data pengguna saat ini
    const currentUserData = userDoc.data();

    const updates = {};

    // Jika ada perubahan nama dan berbeda dari yang sudah ada
    if (nama && nama !== currentUserData.nama) {
      updates.nama = nama;
    }

    // Validasi email baru jika ada dan pastikan berbeda dari email yang ada
    if (emailBaru && emailBaru !== currentUserData.email) {
      try {
        const existingUser = await admin.auth().getUserByEmail(emailBaru);
        if (existingUser) {
          return res.status(400).json({ error: 'Email sudah digunakan oleh akun lain' });
        }
      } catch (error) {
        if (error.code !== 'auth/user-not-found') {
          console.error('Error memvalidasi email baru:', error);
          return res.status(500).json({ error: 'Gagal memvalidasi email baru' });
        }
      }

      // Perbarui email di Firebase Authentication jika berbeda
      try {
        await admin.auth().updateUser(uid, { email: emailBaru });
        updates.email = emailBaru;
      } catch (authError) {
        console.error('Gagal mengubah email di Firebase Authentication:', authError);
        return res.status(500).json({ error: 'Gagal mengubah email di Firebase Authentication' });
      }
    }

    // Perbarui password hanya jika passwordBaru ada
    if (passwordBaru) {
      try {
        await admin.auth().updateUser(uid, { password: passwordBaru });
      } catch (authError) {
        console.error('Gagal mengubah password di Firebase Authentication:', authError);
        return res.status(500).json({ error: 'Gagal mengubah password' });
      }
    }

    // Perbarui dokumen pengguna di Firestore jika ada perubahan
    if (Object.keys(updates).length > 0) {
      await userRef.update(updates);
    }

    // Jika ada file foto profil yang diunggah
    if (req.file) {
      const file = req.file;
      const filePath = `profile-image/${Date.now()}-${file.originalname}`;
      const fileBuffer = file.buffer;

      try {
        // Mengunggah file ke Cloud Storage
        await bucket.file(filePath).save(fileBuffer);
        const fileUrl = `https://storage.googleapis.com/${bucketName}/${filePath}`;

        // Perbarui URL foto profil di Firestore
        await userRef.update({ fotoProfil: fileUrl });
      } catch (uploadError) {
        console.error('Error saat mengunggah foto profil:', uploadError);
        return res.status(500).json({ error: 'Gagal mengunggah foto profil' });
      }
    }

    res.status(200).json({ message: 'Profil berhasil diperbarui' });
  } catch (error) {
    console.error('Error saat memperbarui profil:', error);
    res.status(500).json({ error: 'Gagal memperbarui profil pengguna' });
  }
};

