import DartConfig from './config/DartConfig';
import initializeLogging from './logging/initializeLogging';

// Initialize DartConfig in global scope for access by scala.js
window.env = DartConfig;

// Create functions to access logging from scala.js app
if (DartConfig.enableLogging) {
  initializeLogging();
}
