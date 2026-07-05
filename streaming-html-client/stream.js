const endpoints = {
  webflux: 'http://localhost:8081/api/wikipedia/recent-changes',
  mvc: 'http://localhost:8082/api/wikipedia/recent-changes',
  mvcSseEmitter: 'http://localhost:8083/api/wikipedia/recent-changes',
  quarkus: 'http://localhost:8084/api/wikipedia/recent-changes',
  micronaut: 'http://localhost:8085/api/wikipedia/recent-changes'
};

function buildUrl(filters) {
  const endpoint = endpoints[filters.source] || endpoints.webflux;
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
