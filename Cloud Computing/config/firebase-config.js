// config/firebase-config.js
const admin = require('firebase-admin');

const serviceAccount = {
  "type": "service_account",
  "project_id": "project-nyoba-sapa",
  "private_key_id": "9108a2d56927f01411c4920b9380b4362a43864e",
  "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC5gMd/iWq2df5S\nb01EEFZ6846ogiu6w0Pe9bdcivG/X2pN2jzy+6CX+FHK146Hq+/V3tnjEQKapfYK\nF5XNq+ioFT5WkCgyIfzdqquTTbEcoFhxVW4/Q6LJdRActu/xNMj4tSURMFY6mVoC\n63CGS/cZ7c7n2ammveEVTODORYf0O6qp2Wwm8jBHQd0FqpXV7JujBhMIjt3Z9Kpz\nR3UJ7mNjZxoIhjWgIyWDKPn1sCBvEYCiIRTWQqjVRQ0c41TpsA1psrTGegEzEAcu\nZeCipEaMVAd2AYF3pFAhfJeXQzYJUnBTAqqHO66XUVDYyRSeNn6b1xresXTmBhse\nWw4hllM9AgMBAAECggEAFFzYiV1yQ1ksc6wYFTKd6500ubagSj4JAz6BxDzUUKeO\nqcEdE6cfd88gW13SHX54M0flLxG88XDqUy1Hmm4WeezrDOGCKA6nDUYg02ih2nyX\neMlh8rvf8LFS9XP3XsjmjraDvSFLNd+Di/KARZ2tIEGEhtrTzqPlQ0tPGhVz2RFL\nXyQUIi70iprkBJNnF49uf15pcuP05rPfA+CdBhiyu3H8i13UYVKDCa+LUhNkej2P\nW6W3kTL5M6aDOAgisfyFTUcRLbvIoNOgtOryHpKO27fjeeZlUXVY1a9JOD2n8kwB\nxudhTtA7S8d5inh0xqtU4aOvGUpcNrX1m+J6F9/cmQKBgQD5tbc9sOzQnLqNPE5h\nfzY9g1F9sgfkzeJ6c608l3Rw/kshtD1BgOCwBQwrzrxa4axoElzcAGvLesSHYAw1\nZCQCR6jizJnu2Fkec0BRsNq86T7beqhEyc3TnOGpAoE3UFS2MZOZFknqKYXSGC2e\nCRjYrFzmFmwqg0gxF/U1j1gJQwKBgQC+LQSr7NY7q2ce1x3e+Js/60qnFZHejF0j\n1lY4fjq30QyY++DE5Scd3/N9mcvBFObs0equlvoCMpXp7R3pbYTzp2YKdPdCqEf4\nlSdxbQfCxu0PczRBmFIZV0Pt9RdkkbTqx1yy2UvvszrUcYl74VXjiNRaU1te/K1k\nKLLsoPopfwKBgQDyNhnMpB3A6BVnw7Q/i9Y/6m2UH6jaff4NSsEhwcL3iyGzSpIU\nRH8tHWhF4dz/xmCl/hHGcD4e/DE/IqIU89Kdx4aAn3c8nuwqxh4AXyx9Cz2mRJ6N\nQrX8afLVLhHKZogUxZfmaSE/GhGszTkKTsaj/OSLZfp7biYLQLLfxe/d4wKBgQC6\nILlnwz7R26nrp0LXDZ506sZ/zT6c/+fQwWBVnkW9zl50BGEyk8y7EorvLO0cpmZ0\n9eaoxZ8fM39CGg6ifcwX5IicR2oQ1T7Lbr/R5ZIo8iTmcCx+3qPLpXsYlD428dyN\nxF1XthD6IwK7FfwnQJ2dOjgLx3Wv+8GAO80Sh2MP5QKBgA3oyRVkMEj839ivF8zA\noEkRavUYgSN07eRaPJWSlKwnK4N47hzOF70RyD86adwXjNEZe82QoZKxLwrtDGiE\n1EudbulNBqFKFMYjfzk9sg3OteeeKgzyP8vnukLHQkE6UYXaKraTxBF4n8OfzUuc\nIiBFtW8pF0rNgsgk/cZuKyXf\n-----END PRIVATE KEY-----\n",
  "client_email": "firebase-adminsdk-x602y@project-nyoba-sapa.iam.gserviceaccount.com",
  "client_id": "113236393136490050305",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-x602y%40project-nyoba-sapa.iam.gserviceaccount.com",
  "universe_domain": "googleapis.com"
}

if (!admin.apps.length) {
  admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
  });
}

const db = admin.firestore();
const auth = admin.auth();
module.exports = {db, auth};