export default function getTenantsFromIdToken({ group }) {
  return group.map((grp) => {
    const splitGrp = grp.replace('/', '').split('/');
    const id = splitGrp[0].trim();
    if (id === 'program-manager') return 'global';
    return id;
  });
}
