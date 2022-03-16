export default () => {
  // This will be overwitten by scalajs app
  window.getLogs = () => [];

  window.printLogs = () => {
    const logs = window.getLogs();
    logs.forEach((log, i) => {
      if ('message' in log) console.log(`${i}: ${log.message}`);
      else console.log(`${i}: No Message`);
      if ('exception' in log) {
        console.log(`\t${log.exception.className}: ${log.exception.message}`);
        if ('exception' in log.exception) {
          console.log(log.exception.exception);
        }
      }
      window.console.log('');
    });
  };

  window.readLog = (i) => {
    const logs = window.getLogs();
    if (i in logs) {
      const entry = logs[i];
      if ('message' in entry) {
        console.log(entry.message);
      }
      if ('exception' in entry) {
        console.log(`${entry.exception.className}: ${entry.exception.message}`);
      }
    } else {
      console.log(`Entry ${i} does not exist`);
    }
  };

  window.readLastLog = () => {
    const logs = window.getLogs();
    const size = logs.length;
    if (size > 0) {
      const entry = logs[size - 1];
      if ('message' in entry) {
        console.log(entry.message);
      }
      if ('exception' in entry) {
        console.log(`${entry.exception.className}: ${entry.exception.message}`);
      }
    } else {
      console.log('No log entries yet');
    }
  };

  window.printStackTrace = (i) => {
    const logs = window.getLogs();
    if (i in logs) {
      const entry = logs[i];
      if ('exception' in entry) {
        console.log(`\t${entry.exception.className}: ${entry.exception.message}`);
        throw entry.exception.exception;
      } else {
        console.log(`Entry ${i} does not contain an exception`);
      }
    } else {
      console.log(`Entry ${i} does not exist`);
    }
  };

  window.printLastStackTrace = () => {
    const logs = window.getLogs();
    const size = logs.length;
    if (size > 0) {
      const entry = logs[size - 1];
      if ('exception' in entry) {
        console.log(`\t${entry.exception.className}: ${entry.exception.message}`);
        throw entry.exception.exception;
      } else {
        console.log('Last entry does not contain an exception');
      }
    } else {
      console.log('No log entries yet');
    }
  };
};
