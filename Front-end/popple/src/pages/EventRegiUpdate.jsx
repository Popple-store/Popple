import { useRef, useState } from "react";
import { LuFilePlus } from "react-icons/lu";
import FileCarousel from "../components/exhibition/FileCarousel";
import { eventAPI } from "../api/services/Event";
import { useNavigate } from "react-router-dom";
import Markdown from "../components/common/Markdown";
import { poppleAlert } from "../utils/PoppleAlert";

export default function EventRegiUpdate() {

  const inputStyle =
    "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg inline-block p-2.5 mb-10";

  const fileMax = 5;
  // param으로 넘겨서
  const queryParams = new URLSearchParams(location.search);
  // key값이 id 인 것의 value값을 가져옴
  const exhibitionId = queryParams.get("id");

  //드래그앤 드랍 상태 관리
  const [isActive, setIsActive] = useState(false);
  const [uploadPossible, setUploadPossible] = useState(true);
  //드래그앤드랍 이벤트 핸들러
  const handleDragStart = () => setIsActive(true);
  const handleDragEnd = () => setIsActive(false);
  const handleDrop = (e) => {
    e.preventDefault();
    setIsActive(false);
    const files = Array.from(e.dataTransfer.files);
    onUpload2(files);
  };
  const handleDragOver = (e) => {
    if (!uploadPossible) return false;
    e.preventDefault();
    setIsActive(true);
  };

  //상세 이미지 상태 관리
  const [preview2, setPreview2] = useState([]);
  const onUpload2 = (files) => {
    if (preview2.length >= fileMax) {
      setUploadPossible(false);
      return;
    }
    const previewPromises = files.map((file) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      return new Promise((resolve) => {
        reader.onload = () => resolve(reader.result);
      });
    });
  
    Promise.all(previewPromises).then((newPreviews) => {
      setPreview2((prev) => {
        const updatedPreviews = [...prev, ...newPreviews];
        if (updatedPreviews.length > fileMax) {
          poppleAlert.alert("", `파일은 최대 ${fileMax}개까지 업로드 가능합니다.`);
          updatedPreviews.splice(fileMax);
        }
        return updatedPreviews;
      });
    });
    setInfo((prev) => ({ ...prev, eventImage: files }));
  };

  //포스터 이미지 상태 관리
  const [preview, setPreview] = useState(null);

  const onUpload = (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const reader = new FileReader();

    reader.readAsDataURL(file);
    reader.onload = () => {
      setPreview(reader.result); // 파일의 컨텐츠를 preview에 저장
    };
    setInfo((prev) => ({ ...prev, eventPoster: file }));
  };

  //파일 이미지 삭제
  function deleteImg(index) {
    const deletePreview2 = [...preview2];
    deletePreview2.splice(index, 1);
    setPreview2(deletePreview2);
  }

  
  //입력 정보
  const [info, setInfo] = useState({
    eventImage: [],
    eventPoster: "",
    summary: "",
    exId: exhibitionId,
    description: "",
    eventName: "",
    startAt: "",
    endAt: "",
  });
  const navigate = useNavigate();
  //등록 핸들러
  const handleSubmit = async (event) => {
    try {
      event.preventDefault(); // 기본 폼 제출 방지
      const formData = new FormData();

      // 포스터 파일 추가
      if (info.eventPoster) {
        formData.append("eventPoster", info.eventPoster);
      }
      // 이미지 파일 추가
      if (info.eventImage) {
        info.eventImage.forEach((img, index) => {
          formData.append(`eventImage`, img);
        });
      }
      formData.append("exId", info.exId);
      formData.append("startAt", info.startAt);
      formData.append("endAt", info.endAt);
      formData.append("description", info.description);
      formData.append("summary", info.summary);
      formData.append("eventName", info.eventName);

      const res = await eventAPI.regist(formData);
      if (res.status === 201) {
        poppleAlert.alert("","이벤트가 등록되었습니다.");
        navigate("/");
      }
    } catch (error) {
      poppleAlert.alert("","이벤트 등록에 실패하였습니다.");
      console.error(error, error.message)
    }
  };

  // 마크다운 입력 핸들러
  const handleMarkDown = (name, value) => {
    setInfo((prev) => ({ ...prev, [name]: value }));
  };

  //날짜 지정 (오늘날짜)
  const today = new Date().toISOString().split("T")[0];

  const goRegist = (event) => {
    // 만약에 필수값이 모두 입력되었으면
    const { eventName, startAt, endAt, description, eventPoster, summary} = info
    const requiredValueCheck = eventName && description && startAt && endAt && eventPoster && summary;
    if (requiredValueCheck) {
      handleSubmit(event);
    } else {
      poppleAlert.alert("", "필수값을 모두 입력해주세요.");
    }
  }

  const fileInputRef = useRef(null);

  return (
    <>
      <p className="text-lg mb-2 mt-10 ">팝업/전시 등록</p>
      <hr className="w-full" />
      <div className="mt-2">이벤트 정보</div>

      <div className="flex flex-col w-5/6 mx-auto gap-5 mt-16">
        <div className="grid grid-cols-2 gap-x-20 gap-y-10">
          <div className="flex flex-col gap-10">
            <div>
              <label className="text-sm" htmlFor="1">
                이벤트명 <span className="text-red-500">*</span>
              </label>
              <input
                id="eventName"
                value={info.eventName}
                onChange={(e) =>
                  setInfo({ ...info, eventName: e.target.value })
                }
                className="w-full bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg block p-2.5"
              />
            </div>
            <div>
              <label className="text-sm" htmlFor="1">
                요약설명 <span className="text-red-500">*</span>
              </label>
              <textarea
                id="summary"
                onChange={(e) => setInfo({ ...info, summary: e.target.value })}
                value={info.summary}
                className="w-full bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg block p-2.5"
              />
            </div>
            <div className="flex ">
              <div className="flex flex-col mt-1">
                <label className="text-sm" htmlFor="start">
                  이벤트 시작일<span className="text-red-500">*</span>
                </label>
                <input
                  id="startAt"
                  type="date"
                  value={info.startAt}
                  onChange={(e) =>
                    setInfo({ ...info, startAt: e.target.value })
                  }
                  className="bg-gray-50 border rounded-lg p-2.5 text-xs"
                  required
                  placeholder="시작일"
                  min={`${today}`}
                />
              </div>
              <span className="ml-7 mr-7 mt-7">~</span>
              <div className="flex flex-col mt-1">
                <label className="text-sm" htmlFor="end">
                  이벤트 종료일<span className="text-red-500">*</span>
                </label>
                <input
                  id="endAt"
                  onChange={(e) => setInfo({ ...info, endAt: e.target.value })}
                  value={info.endAt}
                  type="date"
                  className="bg-gray-50 border rounded-lg inline p-2.5 text-xs"
                  required
                  placeholder="종료일"
                  min={`${today}`}
                />
              </div>
            </div>
          </div>
          <div>
            <label className="text-sm" htmlFor="1">
              상세설명 <span className="text-red-500">*</span>
            </label>
            <div>
              <Markdown
                content={info.description}
                contentChange={(e) => handleMarkDown("description", e)}
              />
            </div>
          </div>
        </div>
        <div className="grid grid-cols-2 gap-x-20 gap-y-10 my-10">
          <div>
            <label htmlFor="poster" className="text-sm">
              포스터<span className="text-red-500">*</span>
            </label>
            <label className={`${inputStyle} py-5 flex justify-center cursor-pointer h-48`}>
              <input
                id="poster"
                className="hidden w-fit h-fit"
                type="file"
                onChange={onUpload}
                accept="image/*"
              />
              {preview ? (
                <img className="w-[250px] h-auto" src={preview} alt="포스터" />
              ) : (
                <LuFilePlus className="w-full h-full" />
              )}
            </label>
          </div>
          <div>
            <label htmlFor="image" className="text-sm">
              상세 이미지
            </label>
            <div className="h-full">
              <label
                className={`preview ${
                  isActive ? "active" : " "
                } w-full h-full m-auto bg-white rounded-md border-dashed border p-3 flex justify-center cursor-pointer`}
                onDragEnter={handleDragStart}
                onDragOver={handleDragOver}
                onDrop={handleDrop}
                onDragLeave={handleDragEnd}
                onClick={() => document.getElementById("image").click()}
              >
                <input
                  type="file"
                  id="image"
                  ref={fileInputRef}
                  className="hidden"
                  multiple
                  onChange={(e) => onUpload2(Array.from(e.target.files))}
                  accept="image/*"
                />
                {preview2.length > 0 ? (
                  <FileCarousel preview2={preview2} deleteImg={deleteImg} />
                ) : (
                  <div className="flex flex-col rounded-lg justify-center text-center items-center">
                    <p className="font-medium text-lg my-5 mb-2.5">
                      클릭 혹은 파일을 이곳에 드랍
                    </p>
                    <p className="m-0 text-sm">파일당 최대 3MB</p>
                  </div>
                )}
              </label>
              <div className="text-xs">
                {preview2.length}/{fileMax}
              </div>
            </div>
          </div>
        </div>
        <div>
          <hr className="w-full mt-10" />
          <div className="flex justify-between">
            <button className="border p-3 mt-10 rounded-lg hover:bg-popple hover:text-white"
              onClick={()=>navigate("/")}
            >
              취소</button>
            <button
              type="submit"
              className="border p-3 mt-10 rounded-lg hover:bg-popple hover:text-white"
              onClick={goRegist}
            >
              등록
            </button>
          </div>
        </div>
      </div>
    </>
  );
}
