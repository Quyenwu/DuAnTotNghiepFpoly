import { DownOutlined, GlobalOutlined } from "@ant-design/icons";
import { Dropdown, Space } from "antd";
import React from "react";

export default function Header() {
  const items = [
    {
      label: (
        <a
          onClick={(e) => {
            e.preventDefault();
            handleLogout();
          }}
        >
          Đăng xuất
        </a>
      ),
      key: "0",
    },
  ];
  return (
    <>
      <header className="bg-white w-full h-[80px]  flex items-center justify-between px-[120px]">
        <img src="" />

        <div className="flex items-center gap-3">
          
            <GlobalOutlined style={{width:24, height:24}} />
            <p>VN</p>
            <DownOutlined />
          <div>
          <Dropdown
            menu={{
              items,
            }}
            trigger={["click"]}
          >
            <a onClick={(e) => e.preventDefault()}>
              <Space className="flex items-center cursor-pointer">
                <img src="" />
                
              </Space>
            </a>
          </Dropdown>
          </div>
        </div>
      </header>
    </>
  );
}
