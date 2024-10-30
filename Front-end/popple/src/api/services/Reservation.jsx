import api from "../api";

export const reservationAPI = {
  create : (data) => api.post("/reservation", data),
  getList : () => api.get(`/reservation`),
  getReserverList : (id) => api.get(`/reservation/reserver-list/${id}`),
  cancel : (id) => api.patch(`/reservatio/cancel/${id}`),
  check : (id) => api.patch(`/reservatio/check`, id),
}