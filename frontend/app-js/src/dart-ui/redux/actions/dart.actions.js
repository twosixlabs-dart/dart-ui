import {
  OPEN_MENU,
  CLOSE_MENU,
  SAVE_TOKEN,
  CHOOSE_TENANT,
  SET_TENANTS,
} from './dart.types';

export const openMenu = (element) => ({
  type: OPEN_MENU,
  payload: element,
});

export const closeMenu = () => ({
  type: CLOSE_MENU,
});

export const saveToken = (token, idTokenObj) => ({
  type: SAVE_TOKEN,
  token,
  idTokenObj,
});

export const chooseTenant = (tenantId) => ({
  type: CHOOSE_TENANT,
  tenantId,
});

export const setTenants = (tenants) => ({
  type: SET_TENANTS,
  tenants,
});
