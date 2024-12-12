require('dotenv').config();  
const express = require('express');
const routes = require('./routes/routes');  
const PORT = process.env.PORT || 3000;

const cors = require('cors');
const app = express();

app.use(express.json()); 
app.use(cors({
  origin: ['https://sapa-api-733973953931.asia-southeast2.run.app',], // Daftar domain yang diizinkan
})); 

app.use('/', routes);  

app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
