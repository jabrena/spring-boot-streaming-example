const endpoint = 'http://localhost:8080/api/wikipedia/recent-changes';

function buildUrl(filters) {
  const url = new URL(endpoint);

  if (filters.wiki) {
    url.searchParams.set('wiki', filters.wiki);
  }
  if (filters.limit) {
    url.searchParams.set('limit', filters.limit);
  }
  if (filters.includeBots) {
    url.searchParams.set('includeBots', 'true');
  }

  return url.toString();
}

export function connectToRecentChanges(filters, handlers) {
  const source = new EventSource(buildUrl(filters));

  source.onopen = handlers.onOpen;
  source.onmessage = (event) => handlers.onMessage(JSON.parse(event.data));
  source.onerror = handlers.onError;

  return source;
}
