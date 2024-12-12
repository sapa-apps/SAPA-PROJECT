const admin = require('firebase-admin');

const authMiddleware = async (req, res, next) => {
  const authHeader = req.headers.authorization;

  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return res.status(401).send('Akses ditolak, token tidak tersedia');
  }

  const token = authHeader.split(' ')[1];

  try {
    // Verifikasi token dengan Firebase Admin SDK
    const decodedToken = await admin.auth().verifyIdToken(token);
    req.user = { uid: decodedToken.uid, email: decodedToken.email || null }; // Menyimpan UID dan email ke dalam req.user
    next();
  } catch (error) {
    console.error('Error saat memverifikasi token:', error);
    res.status(401).send('Token tidak valid');
  }
};

module.exports = authMiddleware;
