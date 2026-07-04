const maxVisibleEvents = 30;

const status = document.querySelector('#status');
const statusText = document.querySelector('#statusText');
const events = document.querySelector('#events');
const connectButton = document.querySelector('#connect');
const disconnectButton = document.querySelector('#disconnect');
const wikiInput = document.querySelector('#wiki');
const limitInput = document.querySelector('#limit');
const includeBotsInput = document.querySelector('#includeBots');

function setStatus(text, state) {
  status.classList.remove('connected', 'error');
  if (state) {
    status.classList.add(state);
  }
  statusText.textContent = text;
}

function setControlState(connected) {
  connectButton.disabled = connected;
  disconnectButton.disabled = !connected;
}

export function getFilters() {
  return {
    wiki: wikiInput.value.trim(),
    limit: limitInput.value.trim(),
    includeBots: includeBotsInput.checked
  };
}

export function setConnecting() {
  setControlState(true);
  setStatus('Connecting', null);
}

export function setConnected() {
  setControlState(true);
  setStatus('Connected', 'connected');
}

export function setDisconnected() {
  setControlState(false);
  setStatus('Disconnected', null);
}

export function setError() {
  setControlState(false);
  disconnectButton.disabled = false;
  setStatus('Connection error', 'error');
}

export function clearEvents() {
  events.innerHTML = '';
}

export function renderEvent(change) {
  const article = document.createElement('article');
  article.className = 'event';

  const topline = document.createElement('div');
  topline.className = 'event-topline';

  const metadata = document.createElement('span');
  metadata.textContent = `${change.wiki || 'unknown'} · ${change.type || 'change'}`;

  const user = document.createElement('span');
  user.textContent = change.user || 'anonymous';

  topline.append(metadata, user);

  const title = document.createElement('h2');
  title.className = 'event-title';

  if (change.title_url) {
    const link = document.createElement('a');
    link.href = change.title_url;
    link.target = '_blank';
    link.rel = 'noreferrer';
    link.textContent = change.title || change.title_url;
    title.appendChild(link);
  } else {
    title.textContent = change.title || 'Untitled change';
  }

  const comment = document.createElement('p');
  comment.className = 'event-comment';
  comment.textContent = change.comment || 'No comment';

  article.append(topline, title, comment);
  events.prepend(article);

  while (events.children.length > maxVisibleEvents) {
    events.lastElementChild.remove();
  }
}

export function wireControls(handlers) {
  connectButton.addEventListener('click', handlers.connect);
  disconnectButton.addEventListener('click', handlers.disconnect);
}
