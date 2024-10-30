import api from "../api";

export const exhibitionAPI = {
  regist : (data) => api.post("/exhibition/resist", data),
  update : (data) => api.patch("/exhibition",data),
  delete : (data) => api.patch("/exhibition/delete", data),
  getlist : (id) => api.get(`/exhibition/${id}`),
  get : (id) => api.get(`/exhibition/detail/${id}`),
  my : (data) => api.get("/exhibition/my-exhibition",data),
  gettype : (data) => api.get("exhibition-type",data)
}