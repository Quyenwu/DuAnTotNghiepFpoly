import React from "react";
import Header from "./Header";
import Footer from "./Footer";
import { Outlet } from "react-router";

export default function UserLayout() {
  return (
    <div className="min-h-screen">
      <Header />
      <div className="flex h-full">
        <div className="bg-[#f3f3f9] flex-1 max-h-[100vh-64px] overflow-auto">
          <Outlet />
        </div>
      </div>

      <Footer />
    </div>
  );
}
