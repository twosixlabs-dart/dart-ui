export default function getConfig(component) {
  if (component === 'common') return window.env[component];

  return {
    ...window.env.common,
    ...window.env[component],
  };
}
