import {connectToRecentChanges} from './stream.js';
import {
  clearEvents,
  getFilters,
  renderEvent,
  setConnected,
  setConnecting,
  setDisconnected,
  setError,
  wireControls
} from './ui.js';

let stream;

function connect() {
  disconnect();
  clearEvents();
  setConnecting();

  stream = connectToRecentChanges(getFilters(), {
    onOpen: setConnected,
    onMessage: renderEvent,
    onError: setError
  });
}

function disconnect() {
  if (stream) {
    stream.close();
    stream = undefined;
  }

  setDisconnected();
}

wireControls({connect, disconnect});
